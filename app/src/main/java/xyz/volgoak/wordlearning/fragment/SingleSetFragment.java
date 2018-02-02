package xyz.volgoak.wordlearning.fragment;


import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
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

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.StorageContract;
import xyz.volgoak.wordlearning.databinding.FragmentSingleSetBinding;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Word;
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

    private FragmentSingleSetBinding mBinding;
    private FragmentListener mFragmentListener;

    private List<Word> mWords;

    private long mSetId;
    private boolean mSingleFragMode;

    @Inject
    DataProvider mDataProvider;
    private WordsRecyclerAdapter mRecyclerAdapter;
    private ActionMode mActionMode;
    private WordsActionModeCallback mWordsCallBack;
    private boolean mSetInDictionary;
    private String mSetName;

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
        WordsApp.getsComponent().inject(this);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_set, container, false);

        manageTrainingStatusMenu();
        loadSetInformation();
        prepareRecycler();

        if (mWordsCallBack != null) {
            ((AppCompatActivity) getActivity()).startSupportActionMode(mWordsCallBack);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerAdapter.changeData(mDataProvider.getWordsBySetId(mSetId));
        if (mSingleFragMode) {
            mBinding.setToolbar.setNavigationIcon(R.drawable.ic_back_toolbar);
            mBinding.setToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
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

    private void loadSetInformation() {
        Set set = mDataProvider.getSetById(mSetId);
        Log.d(TAG, "loadSetInformation: theme " + set.getThemeCodes());
        //set title
        mSetName = set.getName();
        mBinding.collapsingToolbarSetAct.setTitle(mSetName);

        //load title image
        String imageRes = set.getImageUrl();

        File imageDir = new File(getActivity().getFilesDir(), StorageContract.IMAGES_FOLDER);
        File imageFile = new File(imageDir, imageRes);

        Glide.with(this)
                .load(imageFile)
                .centerCrop()
                .crossFade().error(R.drawable.def_title)
                .into(mBinding.setIvTitle);


        int setStatus = set.getStatus();
        mSetInDictionary = setStatus == DatabaseContract.Sets.IN_DICTIONARY;

        prepareSetStatusFabs();

        mBinding.setAddFab.setOnClickListener((v) -> addOrRemoveSetFromDictionary());

        mBinding.setResetFab.setOnClickListener((v) -> resetSetProgress());

        mBinding.setTrainingFab.setOnClickListener((v) -> showCoolDialog());
    }

    private void prepareSetStatusFabs() {
        mBinding.setResetFab.setVisibility(mSetInDictionary ? View.VISIBLE : View.GONE);
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
        mWords = mDataProvider.getWordsBySetId(mSetId);

        mBinding.rvSetAc.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvSetAc.setLayoutManager(llm);

        mRecyclerAdapter = new WordsRecyclerAdapter(getContext(), mWords, mBinding.rvSetAc);

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

    // TODO: 25.08.2017 manage this method/ I don't remember why I broke it
    public void addOrRemoveSetFromDictionary() {
        View.OnClickListener snackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        int newStatus = mSetInDictionary ? DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
        mSetInDictionary = !mSetInDictionary;

        Set set = mDataProvider.getSetById(mSetId);
        set.setStatus(newStatus);

        mDataProvider.updateSets(set);
        prepareSetStatusFabs();
        manageTrainingStatusMenu();

        int messageId = mSetInDictionary ? R.string.set_added_message : R.string.set_removed_message;
        String message = getString(messageId, mSetName);
        Snackbar.make(getView().findViewById(R.id.coordinator_setact), message, BaseTransientBottomBar.LENGTH_LONG).show();

        mRecyclerAdapter.changeData(mDataProvider.getWordsBySetId(mSetId));
    }

    public void resetSetProgress() {

        View.OnClickListener snackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Word> words = mDataProvider.getWordsBySetId(mSetId);
                for(Word word : words) {
                    word.resetProgress();
                }
                mDataProvider.updateWords(words.toArray(new Word[words.size()]));
                mRecyclerAdapter.changeData(mDataProvider.getWordsBySetId(mSetId));
            }
        };

        Snackbar.make(getView().findViewById(R.id.coordinator_setact), R.string.reset_progress_question, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reset, snackListener).show();
    }

    public void changeWordsStatus(List<Integer> positions, int newStatus) {
        if (positions.size() == 0) return;
        Word[] wordsArray = new Word[positions.size()];
        for (int a = 0; a < positions.size(); a++) {
            Word word = mWords.get(positions.get(a));
            word.setStatus(newStatus);
            wordsArray[a] = word;
        }

        mDataProvider.updateWords(wordsArray);
    }

    public void resetWordsProgress(List<Integer> positions) {
        if (positions.size() == 0) return;
        Word[] wordsArray = new Word[positions.size()];
        for (int a = 0; a < positions.size(); a++) {
            Word word = mWords.get(positions.get(a));
            word.resetProgress();
            wordsArray[a] = word;
        }

        mDataProvider.updateWords(wordsArray);
    }

    public void updateAdapterCursor() {
        mRecyclerAdapter.changeData(mDataProvider.getWordsBySetId(mSetId));
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
                    changeWordsStatus(choiceMode.getCheckedList(), DatabaseContract.Words.IN_DICTIONARY);
                    mActionMode.finish();
                    return true;
                case R.id.menu_remove_setfrag_action:
                    changeWordsStatus(choiceMode.getCheckedList(), DatabaseContract.Words.OUT_OF_DICTIONARY);
                    mActionMode.finish();
                    return true;
                case R.id.menu_reset_setfrag_action:
                    resetWordsProgress(choiceMode.getCheckedList());
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
