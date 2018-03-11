package xyz.volgoak.wordlearning.fragment;


import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.DataEntity;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;
import xyz.volgoak.wordlearning.model.WordsViewModel;
import xyz.volgoak.wordlearning.recycler.RecyclerAdapter;
import xyz.volgoak.wordlearning.recycler.SetsRecyclerAdapter;
import xyz.volgoak.wordlearning.recycler.SingleChoiceMode;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class WordSetsFragment extends Fragment implements SetsRecyclerAdapter.SetStatusChanger,
        RecyclerAdapter.AdapterClickListener, RecyclerAdapter.AdapterLongClickListener {

    public static final String TAG = "WordSetsFragment";
    public static final String EXTRA_PARTSCREEN_MODE = "extra_screen_mode";
    public static final String SAVED_POSITION = "saved_position";
    public static final String SAVED_THEME = "saved_theme";

    private FragmentListener mFragmentListener;
    private SetsFragmentListener mSetsFragmentListener;
    private SetsRecyclerAdapter mRecyclerAdapter;

    private RecyclerView mRecyclerView;

    private boolean mPartScreenMode = true;

    private int mRvSavedPosition;
    private String mSelectedTheme = DatabaseContract.Themes.THEME_ANY;
    private List<Theme> mThemes;

    private WordsViewModel viewModel;

    public static WordSetsFragment newInstance(boolean part_mode) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_PARTSCREEN_MODE, part_mode);

        WordSetsFragment fragment = new WordSetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public WordSetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_word_sets, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbarSets);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mPartScreenMode = getArguments().getBoolean(EXTRA_PARTSCREEN_MODE, false);
        }

        if (savedInstanceState != null) {
            mRvSavedPosition = savedInstanceState.getInt(SAVED_POSITION, 0);
            mSelectedTheme = savedInstanceState.getString(SAVED_THEME, DatabaseContract.Themes.THEME_ANY);
        }

        viewModel = ViewModelProviders.of(getActivity()).get(WordsViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }

        mRecyclerView = getView().findViewById(R.id.rv_setsfrag);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        viewModel.getThemes().observe(this, (themes -> mThemes = themes));
        initRecycler(mSelectedTheme);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SetsFragmentListener) {
            mSetsFragmentListener = (SetsFragmentListener) context;
        } else {
            throw new RuntimeException("Activity must implement WordsFragmentListener");
        }

        if (context instanceof FragmentListener) {
            mFragmentListener = (FragmentListener) context;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sets_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort_by_theme:
                showThemesList();
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showThemesList() {
        View view = getActivity().findViewById(R.id.item_sort_by_theme);
        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        MenuItem allThemes = popupMenu.getMenu().add(1, -1, 0, R.string.all_themes);
        allThemes.setCheckable(true);
        allThemes.setChecked(true);

        for (int i = 0; i < mThemes.size(); i++) {
            Theme theme = mThemes.get(i);
            String name = theme.getName();
            MenuItem item = popupMenu.getMenu().add(1, i, 0, name);
            item.setCheckable(true);
            if (mSelectedTheme.equals(theme.getCode())) {
                item.setChecked(true);
                allThemes.setChecked(false);
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == -1) {
                mSelectedTheme = "";
            } else {
                mSelectedTheme = mThemes.get(item.getItemId()).getCode();
            }
            viewModel.changeTheme(mSelectedTheme);
            return true;
        });

        popupMenu.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSetsFragmentListener = null;
        mFragmentListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.getLayoutManager().scrollToPosition(mRvSavedPosition);
        mRvSavedPosition = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        mRvSavedPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_POSITION, mRvSavedPosition);
        outState.putString(SAVED_THEME, mSelectedTheme);
    }

    public void invokeSetMenu(Set set) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        String name = set.getName();
        final int currentSetStatus = set.getStatus();

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(name);

        /*Button openButton = dialog.findViewById(R.id.dialog_bt_one);
        openButton.setText(R.string.open);
        openButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SingleSetActivity.class);
            intent.putExtra(SingleSetActivity.ID_EXTRA, set.getId());
            startActivity(intent);
            dialog.dismiss();

        });*/

        Button addButton = dialog.findViewById(R.id.dialog_bt_two);

        int actionStringId = currentSetStatus == DatabaseContract.Sets.IN_DICTIONARY ?
                R.string.remove_from_dictionary : R.string.add_to_dictionary;

        addButton.setText(actionStringId);
        addButton.setOnClickListener(v -> {
            changeSetStatus(set);
            dialog.dismiss();
        });

        Button wtButton = dialog.findViewById(R.id.dialog_bt_three);
        wtButton.setVisibility(View.VISIBLE);
        wtButton.setText(R.string.word_translation);
        wtButton.setOnClickListener(v -> {
            mFragmentListener.startTraining(TrainingFabric.WORD_TRANSLATION, set.getId());
            dialog.dismiss();
        });

        Button twButton = dialog.findViewById(R.id.dialog_bt_four);
        twButton.setVisibility(View.VISIBLE);
        twButton.setText(R.string.translation_word);
        twButton.setOnClickListener(v -> {
            mFragmentListener.startTraining(TrainingFabric.TRANSLATION_WORD, set.getId());
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void changeSetStatus(Set set) {

        int currentStatus = set.getStatus();
        int newStatus = currentStatus == DatabaseContract.Sets.IN_DICTIONARY ?
                DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
        String setName = set.getName();

        String message;
        message = newStatus == DatabaseContract.Sets.IN_DICTIONARY ? getString(R.string.set_added_message, setName) :
                getString(R.string.set_removed_message, setName);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        set.setStatus(newStatus);
        viewModel.updateSetStatus(set);
        mRecyclerAdapter.notifyEntityChanged(set);
    }

    @Override
    public void onClick(View root, int position, DataEntity entity) {
        LiveData<Boolean> loadedData = viewModel.changeSet(entity.getId());
        loadedData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                loadedData.removeObserver(this);
                mSetsFragmentListener.startSet(entity.getId(), root.findViewById(R.id.civ_sets));
            }
        });
    }

    @Override
    public boolean onLongClick(View root, int position, DataEntity entity) {
        invokeSetMenu((Set) entity);
        return true;
    }

    public void initRecycler(String themeCode) {

        mRecyclerAdapter = new SetsRecyclerAdapter(getContext(), new ArrayList<>(), mRecyclerView);
        mRecyclerAdapter.setAdapterClickListener(this);
        mRecyclerAdapter.setSetStatusChanger(this);

        if (mPartScreenMode) {
            mRecyclerAdapter.setChoiceMode(new SingleChoiceMode());
        }

        mRecyclerView.setAdapter(mRecyclerAdapter);

        viewModel.getSets(themeCode).observe(this, (list) -> mRecyclerAdapter.changeData(list));
    }

    public interface SetsFragmentListener {
        void startSet(long setId, View shared);
    }
}
