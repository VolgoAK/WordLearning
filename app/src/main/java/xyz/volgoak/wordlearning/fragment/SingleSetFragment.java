package xyz.volgoak.wordlearning.fragment;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.StorageContract;
import xyz.volgoak.wordlearning.databinding.FragmentSingleSetBinding;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.model.SetsViewModel;
import xyz.volgoak.wordlearning.recycler.MultiChoiceMode;
import xyz.volgoak.wordlearning.recycler.WordsRecyclerAdapter;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSetFragment extends Fragment {

    public static final String TAG = SingleSetFragment.class.getSimpleName();

    public static final String EXTRA_SET_ID = "set_id";
    public static final String EXTRA_SINGLE_MODE = "single_mode";
    public static final String SAVED_IS_MULTI_CHOICE = "is_multi_choice";
    public static final String SAVED_CHOICE_MODE = "saved_choice_mode";
    public static final String EXTRA_TRANSITION_NAME = "extra_transition";

    private FragmentSingleSetBinding mBinding;
    private FragmentListener mFragmentListener;

    private List<Word> mWords;

    private long mSetId;
    private boolean mSingleFragMode;

    private WordsRecyclerAdapter mRecyclerAdapter;
    private ActionMode mActionMode;
    private WordsActionModeCallback mWordsCallBack;
    private boolean mSetInDictionary;
    private String mSetName;

    private SetsViewModel viewModel;

    public static SingleSetFragment newInstance(long setId, boolean singleFragmentMode) {
        SingleSetFragment fragment = new SingleSetFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_SET_ID, setId);
        args.putBoolean(EXTRA_SINGLE_MODE, singleFragmentMode);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleSetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSetId = getArguments().getLong(EXTRA_SET_ID);
            mSingleFragMode = getArguments().getBoolean(EXTRA_SINGLE_MODE);
        }

        if (savedInstanceState != null) {
            boolean inMultiChoice = savedInstanceState.getBoolean(SAVED_IS_MULTI_CHOICE, false);
            if (inMultiChoice) {
                mWordsCallBack = new WordsActionModeCallback();
                mWordsCallBack.onRestoreInstanceState(savedInstanceState);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(SetsViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_set, container, false);
        String transitionName = getArguments().getString(EXTRA_TRANSITION_NAME);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.setIvTitle.setTransitionName(transitionName);
        }

        manageTrainingStatusMenu();
        prepareRecycler();

        if (mWordsCallBack != null) {
            ((AppCompatActivity) getActivity()).startSupportActionMode(mWordsCallBack);
        }

        viewModel.getWordsForSet()
                .observe(this, list -> mRecyclerAdapter.changeData(list));
        viewModel.getCurrentSet()
                .observe(this, this::loadSetInformation);

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mSingleFragMode) {
            mBinding.setToolbar.setNavigationIcon(R.drawable.ic_back_toolbar);
            mBinding.setToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
            mBinding.coordinatorSetact.setFitsSystemWindows(true);
        } else {
//            AppBarLayout.LayoutParams layoutParams =(AppBarLayout.LayoutParams) mBinding.collapsingToolbarSetAct.getLayoutParams();
//            layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
//            mBinding.collapsingToolbarSetAct.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            mFragmentListener = (FragmentListener) context;
        } else {
            throw new RuntimeException("Context must implement FragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mWordsCallBack != null) {
            outState.putBoolean(SAVED_IS_MULTI_CHOICE, true);
            mWordsCallBack.onSaveInstanceState(outState);
        }
    }

    private void loadSetInformation(Set set) {
        Log.d(TAG, "loadSetInformation: theme " + set.getThemeCodes());
        //set title
        mSetName = set.getName();
        mBinding.collapsingToolbarSetAct.setTitle(mSetName);

        //load title image
        String imageRes = set.getImageUrl();

        File imageDir = new File(getActivity().getFilesDir(), StorageContract.IMAGES_FOLDER);
        File imageFile = new File(imageDir, imageRes);

        Picasso.with(getContext())
                .load(imageFile)
                .into(mBinding.setIvTitle, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        startPostponedEnterTransition();
                    }
                });



        int setStatus = set.getStatus();
        mSetInDictionary = setStatus == DatabaseContract.Sets.IN_DICTIONARY;

        prepareSetStatusFabs();

        mBinding.setAddFab.setOnClickListener((v) ->
                viewModel.changeCurrentSetStatus().observe(this, this::onStatusChanged));

        mBinding.setResetFab.setOnClickListener((v) -> resetSetProgress());

        mBinding.setTrainingFab.setOnClickListener((v) -> showCoolDialog());
    }

    private void prepareSetStatusFabs() {
        mBinding.setResetFab.setVisibility(mSetInDictionary ? View.VISIBLE : View.INVISIBLE);
        int addDrawableId = mSetInDictionary ? R.drawable.ic_remove_white_24dp : R.drawable.ic_add_white_24dp;
        mBinding.setAddFab.setImageResource(addDrawableId);
    }

    private void manageTrainingStatusMenu() {

    }

    private void showCoolDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.start_training_dialog);

        Toolbar titleToolbar = dialog.findViewById(R.id.dialog_toolbar);
        titleToolbar.setTitle(R.string.training);
        titleToolbar.setNavigationIcon(R.drawable.ic_training_24dp);

        CardView wtCard = dialog.findViewById(R.id.cv_word_trans_dialog);
        wtCard.setOnClickListener((v) -> {
            mFragmentListener.startTraining(TrainingFabric.WORD_TRANSLATION, mSetId);
            dialog.dismiss();
        });

        CardView twCard = dialog.findViewById(R.id.cv_trans_word_dialog);
        twCard.setOnClickListener((v) -> {
            mFragmentListener.startTraining(TrainingFabric.TRANSLATION_WORD, mSetId);
            dialog.dismiss();
        });

        CardView boolCard = dialog.findViewById(R.id.cv_bool_dialog);
        boolCard.setOnClickListener((v) -> {
            mFragmentListener.startTraining(TrainingFabric.BOOL_TRAINING, mSetId);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void prepareRecycler() {

        mBinding.rvSetAc.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvSetAc.setLayoutManager(llm);

        mRecyclerAdapter = new WordsRecyclerAdapter(getContext(), new ArrayList<>(), mBinding.rvSetAc);

        if (mWordsCallBack != null) {
            mRecyclerAdapter.setChoiceMode(mWordsCallBack.choiceMode);
        }

        mRecyclerAdapter.setAdapterClickListener((root, position, id) -> {
            if (mActionMode != null) {
                mActionMode.invalidate();
            }
        });

        mRecyclerAdapter.setAdapterLongClickListener((root, position, id) -> {

            if (mActionMode == null) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                //set action mode to fragment toolbar only in single mode
                if (mSingleFragMode) activity.setSupportActionBar(mBinding.setToolbar);

                mWordsCallBack = new WordsActionModeCallback();
                mWordsCallBack.choiceMode.setChecked(position, true);
                activity.startSupportActionMode(mWordsCallBack);

                return true;
            } else return false;
        });

        mBinding.rvSetAc.setAdapter(mRecyclerAdapter);
    }

    public void onStatusChanged(int status) {
        int messageId = status == DatabaseContract.Sets.IN_DICTIONARY ?
                R.string.set_added_message : R.string.set_removed_message;
        String message = getString(messageId, mSetName);
        Snackbar.make(getView().findViewById(R.id.coordinator_setact), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public void resetSetProgress() {
        Snackbar.make(getView().findViewById(R.id.coordinator_setact), R.string.reset_progress_question, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reset, v -> viewModel.resetCurrentSetProgress()).show();
    }

    public void changeWordsStatus(List<Integer> positions, int newStatus) {
        if (positions.size() == 0) return;
        long time = System.currentTimeMillis();
        Word[] wordsArray = new Word[positions.size()];
        for (int a = 0; a < positions.size(); a++) {
            Word word = mWords.get(positions.get(a));
            word.setStatus(newStatus);
            if(newStatus == DatabaseContract.Words.IN_DICTIONARY) word.setAddedTime(time);
            wordsArray[a] = word;
        }

        viewModel.updateWords(wordsArray);
    }

    public void resetWordsProgress(List<Integer> positions) {
        if (positions.size() == 0) return;
        Word[] wordsArray = new Word[positions.size()];
        for (int a = 0; a < positions.size(); a++) {
            Word word = mWords.get(positions.get(a));
            word.resetProgress();
            wordsArray[a] = word;
        }

        viewModel.updateWords(wordsArray);
    }

    class WordsActionModeCallback implements ActionMode.Callback {

        MultiChoiceMode choiceMode;
//        TextView counter;

        WordsActionModeCallback() {
            choiceMode = new MultiChoiceMode();
        }

        public void onSaveInstanceState(Bundle instanceState) {
            if (choiceMode != null) {
                choiceMode.onSaveInstanceState(instanceState);
                Log.d("Callback", "onSaveInstanceState: size " + choiceMode.getCheckedCount());
            }
        }

        public void onRestoreInstanceState(Bundle savedInstanceState) {
            choiceMode.restoreInstanceState(savedInstanceState);
            Log.d("Callback", "onRestoreInstanceState: size " + choiceMode.getCheckedCount());
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.d("Callback", "onCreateActionMode: ");
            mode.getMenuInflater().inflate(R.menu.menu_set_frag_action_mode, menu);
            mRecyclerAdapter.setChoiceMode(choiceMode);
            mActionMode = mode;
//            counter = new TextView(getContext());
            mode.setTitle(choiceMode.getCheckedCount() + "");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(choiceMode.getCheckedCount() + "");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.menu_add_setfrag_action:
                    viewModel.changeWordsStatus(choiceMode.getCheckedList(), DatabaseContract.Words.IN_DICTIONARY);
                    mActionMode.finish();
                    return true;
                case R.id.menu_remove_setfrag_action:
                    viewModel.changeWordsStatus(choiceMode.getCheckedList(), DatabaseContract.Words.OUT_OF_DICTIONARY);
                    mActionMode.finish();
                    return true;
                case R.id.menu_reset_setfrag_action:
                    viewModel.resetWordsProgress(choiceMode.getCheckedList());
                    mActionMode.finish();
                    return true;

            }
            mActionMode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d("Callback", "onDestroyActionMode: ");
            choiceMode.clearChecks();
            mRecyclerAdapter.setChoiceMode(null);
            mActionMode = null;
            mWordsCallBack = null;
        }
    }
}
