package xyz.volgoak.wordlearning.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.activity.SingleSetActivity;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
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
        RecyclerAdapter.AdapterClickListener, RecyclerAdapter.AdapterLongClickListener{

    public static final String TAG = "WordSetsFragment";
    public static final String EXTRA_PARTSCREEN_MODE = "extra_screen_mode";
    public static final String SAVED_POSITION = "saved_position";
    public static final String SAVED_THEME = "saved_theme";

    private FragmentListener mFragmentListener;
    private SetsFragmentListener mSetsFragmentListener;
    private WordsDbAdapter mDbAdapter;
    private SetsRecyclerAdapter mRecyclerAdapter;

    private RecyclerView mRecyclerView;

    private boolean mPartScreenMode = true;

    private int mRvSavedPosition;
    private int mSelectedTheme = -1;

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
        // Inflate the layout for this fragment
//        mFragmentListener = (FragmentListener) getActivity();
        return inflater.inflate(R.layout.fragment_word_sets, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null) {
            mPartScreenMode = getArguments().getBoolean(EXTRA_PARTSCREEN_MODE, false);
        }

        if(savedInstanceState != null){
            mRvSavedPosition = savedInstanceState.getInt(SAVED_POSITION, 0);
            mSelectedTheme = savedInstanceState.getInt(SAVED_THEME, -1);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        mFragmentListener.setActionBarTitle(getString(R.string.sets));

        mDbAdapter = new WordsDbAdapter();

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv_setsfrag);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        initRecycler(mSelectedTheme);
//
//        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                long setId = mRecyclerAdapter.getItemId(position);
//                mSetsFragmentListener.startSet(setId);
//                return true;
//            }
//        });
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                invokeSetMenu(id);
//            }
//        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SetsFragmentListener){
            Log.d(TAG, "onAttach: FragmentListener set");
            mSetsFragmentListener = (SetsFragmentListener) context;
        }else{
            throw new RuntimeException("Activity must implement WordsFragmentListener");
        }

        if(context instanceof FragmentListener){
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
        switch (item.getItemId()){
            case R.id.item_sort_by_theme :
                showThemesList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showThemesList(){
        View view = getActivity().findViewById(R.id.item_sort_by_theme);
        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        MenuItem allThemes = popupMenu.getMenu().add(1, -1, 0, R.string.all_themes);
        allThemes.setCheckable(true);
        allThemes.setChecked(true);

        List<Theme> themes = mDbAdapter.fetchAllThemes();
        for(Theme theme : themes) {
            String name = theme.getName();
            int code = theme.getCode();
            MenuItem item = popupMenu.getMenu().add(1, code, 0, name);
            item.setCheckable(true);
            if(code == mSelectedTheme){
                Log.d(TAG, "showThemesList: set checked");
                item.setChecked(true);
                allThemes.setChecked(false);
            }
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mSelectedTheme = item.getItemId();
                initRecycler(mSelectedTheme);
                return true;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
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
        mRvSavedPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_POSITION, mRvSavedPosition);
        outState.putInt(SAVED_THEME, mSelectedTheme);
    }

    public void invokeSetMenu(final long setId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        Set set = mDbAdapter.fetchSetById(setId);
        String name = set.getName();
        final int currentSetStatus = set.getStatus();

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(name);

        Button openButton = dialog.findViewById(R.id.dialog_bt_one);
        openButton.setText(R.string.open);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SingleSetActivity.class);
                intent.putExtra(SingleSetActivity.ID_EXTRA, setId);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        Button addButton = dialog.findViewById(R.id.dialog_bt_two);
        int actionStringId = currentSetStatus == DatabaseContract.Sets.IN_DICTIONARY ?
                R.string.remove_from_dictionary :  R.string.add_to_dictionary;

        addButton.setText(actionStringId);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSetStatus(setId);
                dialog.dismiss();
            }
        });


        Button wtButton = dialog.findViewById(R.id.dialog_bt_three);
        wtButton.setVisibility(View.VISIBLE);
        wtButton.setText(R.string.word_translation);
        wtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.startTraining(TrainingFabric.WORD_TRANSLATION, setId);
                dialog.dismiss();
            }
        });

        Button twButton = dialog.findViewById(R.id.dialog_bt_four);
        twButton.setVisibility(View.VISIBLE);
        twButton.setText(R.string.translation_word);
        twButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.startTraining(TrainingFabric.TRANSLATION_WORD, setId);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void changeSetStatus(long setId) {
//        Log.d(TAG, "changeSetStatus: ");
        Set set = mDbAdapter.fetchSetById(setId);
        if(set == null){
//            Log.d(TAG, "changeSetStatus: incorrect id");
            return;
        }

        int currentStatus = set.getStatus();
        int newStatus = currentStatus == DatabaseContract.Sets.IN_DICTIONARY ?
            DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
        String setName = set.getName();

        String message ;
        message = newStatus == DatabaseContract.Sets.IN_DICTIONARY ? getString(R.string.set_added_message, setName) :
                getString(R.string.set_removed_message, setName);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        mDbAdapter.changeSetStatus(setId, newStatus);

        set.setStatus(newStatus);
        mRecyclerAdapter.notifyEntityChanged(set);
    }

    @Override
    public void onClick(View root, int position, long id) {
        Log.d(TAG, "onClick: " + id);
        mSetsFragmentListener.startSet(id, root.findViewById(R.id.civ_sets));
    }

    @Override
    public boolean onLongClick(View root, int position, long id) {
        Log.d(TAG, "on item long click");
        invokeSetMenu(id);
        return true;
    }

    public void initRecycler(int themeCode){
        List<Set> setList;
        if(themeCode == -1){
            setList = mDbAdapter.fetchAllSets();
        }else{
            setList = mDbAdapter.fetchSetsByThemeCode(themeCode);
        }
        mRecyclerAdapter = new SetsRecyclerAdapter(getContext(), setList, mRecyclerView);
        mRecyclerAdapter.setAdapterClickListener(this);
        mRecyclerAdapter.setSetStatusChanger(this);

        if(mPartScreenMode) {
            mRecyclerAdapter.setChoiceMode(new SingleChoiceMode());
        }

        mRecyclerView.setAdapter(mRecyclerAdapter);

    }

    public interface SetsFragmentListener{
        void startSet(long setId, View shared);
    }
}
