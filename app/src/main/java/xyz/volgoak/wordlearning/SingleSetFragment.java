package xyz.volgoak.wordlearning;


import android.app.Activity;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.support.v7.view.ActionMode;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.databinding.FragmentSingleSetBinding;
import xyz.volgoak.wordlearning.recycler.CursorRecyclerAdapter;
import xyz.volgoak.wordlearning.recycler.MultiChoiceMode;
import xyz.volgoak.wordlearning.recycler.SingleChoiceMode;
import xyz.volgoak.wordlearning.recycler.WordsRecyclerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSetFragment extends Fragment {

    public static final String EXTRA_SET_ID = "set_id";
    public static final String EXTRA_SINGLE_MODE = "single_mode";

    private FragmentSingleSetBinding mBinding;

    private long mSetId;
    private boolean mSingleFragMode;

    private WordsDbAdapter mDbAdapter;
    private WordsRecyclerAdapter mRecyclerAdapter;
    private ActionMode mActionMode;
    private boolean mSetInDictionary;
    private String mSetName;

    public static SingleSetFragment newInstance(long setId, boolean singleFragmentMode){
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
        if(getArguments() != null){
            mSetId = getArguments().getLong(EXTRA_SET_ID);
            mSingleFragMode = getArguments().getBoolean(EXTRA_SINGLE_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_set, container, false);
        mDbAdapter = new WordsDbAdapter();
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mSingleFragMode){
            mBinding.setToolbar.setNavigationIcon(R.drawable.ic_back_toolbar);
            mBinding.setToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }else{
//            AppBarLayout.LayoutParams layoutParams =(AppBarLayout.LayoutParams) mBinding.collapsingToolbarSetAct.getLayoutParams();
//            layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
//            mBinding.collapsingToolbarSetAct.setLayoutParams(layoutParams);
        }

        manageTrainingStatusMenu();
        loadSetInformation();
        prepareRecycler();
    }

    @Override
    public void onStop() {
        mRecyclerAdapter.closeCursor();
        super.onStop();
    }

    private void loadSetInformation(){
        Cursor setCursor = mDbAdapter.fetchSetById(mSetId);
        setCursor.moveToFirst();

        //set title
        mSetName = setCursor.getString(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
        mBinding.collapsingToolbarSetAct.setTitle(mSetName);



        //load title image
        String imageRes = setCursor.getString(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_IMAGE_URL));
        StorageReference imageRef = FirebaseStorage.getInstance()
                .getReference(FirebaseContract.TITLE_IMAGES_FOLDER)
                .child(imageRes);

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .centerCrop()
                .crossFade().error(R.drawable.def_title)
                .into(mBinding.setIvTitle);


        int setStatus = setCursor.getInt(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_STATUS));
        mSetInDictionary = setStatus == DatabaseContract.Sets.IN_DICTIONARY;
        setCursor.close();

        prepareSetStatusFabs();

        mBinding.setAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrRemoveSetFromDictionary();
            }
        });


        mBinding.setResetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSetProgress();
            }
        });

    }

    private void prepareSetStatusFabs(){
        mBinding.setResetFab.setVisibility(mSetInDictionary ? View.VISIBLE : View.GONE);
        int addDrawableId = mSetInDictionary ? R.drawable.ic_remove_white_24dp : R.drawable.ic_add_white_24dp;
        mBinding.setAddFab.setImageDrawable(ContextCompat.getDrawable(getContext(), addDrawableId));
    }

    private void manageTrainingStatusMenu(){

    }

    private void prepareRecycler(){
        Cursor cursor = mDbAdapter.fetchWordsBySetId(mSetId);

        mBinding.rvSetAc.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvSetAc.setLayoutManager(llm);

        mRecyclerAdapter = new WordsRecyclerAdapter(getContext(), cursor, mBinding.rvSetAc);
        mRecyclerAdapter.setAdapterClickListener(new CursorRecyclerAdapter.AdapterClickListener() {
            @Override
            public void onClick(View root, int position, long id) {
                //do noting
            }
        });

        mRecyclerAdapter.setAdapterLongClickListener(new CursorRecyclerAdapter.AdapterLongClickListener() {
            @Override
            public boolean onLongClick(View root, int position, long id) {
                if(mActionMode == null) {
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    //set action mode to fragment toolbar only in single mode
                    if (mSingleFragMode) activity.setSupportActionBar(mBinding.setToolbar);

                    ActionMode.Callback callback = new WordsActionMode();
                    activity.startSupportActionMode(callback);

                    return true;
                }else return false;
            }
        });
        mBinding.rvSetAc.setAdapter(mRecyclerAdapter);
    }

    // TODO: 25.08.2017 manage this method/ I don't remember why I broke it
    public void addOrRemoveSetFromDictionary(){
        View.OnClickListener snackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        int newStatus = mSetInDictionary ? DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
        mSetInDictionary = !mSetInDictionary;

        mDbAdapter.changeSetStatus(mSetId, newStatus);
        prepareSetStatusFabs();
        manageTrainingStatusMenu();

        int messageId = mSetInDictionary ? R.string.set_added_message : R.string.set_removed_message;
        String message = getString(messageId, mSetName);
        Snackbar.make(getView().findViewById(R.id.coordinator_setact), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public void resetSetProgress(){

        View.OnClickListener snackListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDbAdapter.resetSetProgress(mSetId);
                mRecyclerAdapter.changeCursor(mDbAdapter.fetchWordsBySetId(mSetId));
            }
        };

        Snackbar.make(getView().findViewById(R.id.coordinator_setact), R.string.reset_progress_question, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reset, snackListener).show();
    }

    class WordsActionMode implements ActionMode.Callback{

        MultiChoiceMode choiceMode = new MultiChoiceMode();
        ActionMode mActionMode;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_set_frag_action_mode, menu);
            mRecyclerAdapter.setChoiceMode(choiceMode);
            mActionMode = mode;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            mActionMode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            choiceMode.clearChecks();
            mRecyclerAdapter.setChoiceMode(null);
        }
    }
}