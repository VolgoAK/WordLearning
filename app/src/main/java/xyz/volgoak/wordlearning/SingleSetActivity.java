package xyz.volgoak.wordlearning;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class SingleSetActivity extends AppCompatActivity {

    public static final String TAG = "SingleSetActivity";
    public static final String ID_EXTRA = "id_extra";

    private SingleSetFragment mSetFragment;
    private long mSetId;
    /*private ActivitySetBinding mBinding;

    private WordsDbAdapter mDbAdapter;
    private DictionaryRecyclerAdapter mRecyclerAdapter;

    private boolean mSetInDictionary;
    private String mSetName;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_single_set);

        FirebaseAuth.getInstance().signInAnonymously();

        mSetId = getIntent().getLongExtra(ID_EXTRA, -1);

        mSetFragment =(SingleSetFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);

        if(mSetFragment == null){
            mSetFragment = SingleSetFragment.newInstance(mSetId, true);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, mSetFragment)
                    .commit();
        }
        /*mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_set);
        setSupportActionBar(mBinding.setToolbar);

        mSetId = getIntent().getLongExtra(ID_EXTRA, -1);
        mDbAdapter = new WordsDbAdapter();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        manageTrainingStatusMenu();
        loadSetInformation();
        prepareRecycler();
    }*/

   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addItem = menu.findItem(R.id.item_add_setact);
        addItem.setTitle(mSetInDictionary ? R.string.remove_from_dictionary : R.string.add_to_dictionary);

        MenuItem resetItem = menu.findItem(R.id.item_reset_setact);
        resetItem.setVisible(mSetInDictionary);

        return super.onPrepareOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
            case R.id.item_add_setact:
                addOrRemoveSetFromDictionary();
                return true;
            case R.id.item_reset_setact:
                resetSetProgress();
                return true;
            case R.id.item_wt_setact :
                startTraining(TrainingFabric.WORD_TRANSLATION);
                return true;
            case R.id.item_tw_setact :
                startTraining(TrainingFabric.TRANSLATION_WORD);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    protected void onStop() {
        mRecyclerAdapter.getCursor().close();
        super.onStop();
    }*/

    /*private void loadSetInformation(){
        Cursor setCursor = mDbAdapter.fetchSetById(mSetId);
        setCursor.moveToFirst();

        //set title
        mSetName = setCursor.getString(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
        getSupportActionBar().setTitle(mSetName);

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
        mBinding.setAddFab.setImageDrawable(ContextCompat.getDrawable(this, addDrawableId));
    }

    private void manageTrainingStatusMenu(){

    }

    private void prepareRecycler(){
        Cursor cursor = mDbAdapter.fetchWordsBySetId(mSetId);

        mBinding.rvSetAc.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvSetAc.setLayoutManager(llm);

        mRecyclerAdapter = new DictionaryRecyclerAdapter(cursor, this);
        mBinding.rvSetAc.setAdapter(mRecyclerAdapter);
    }

    public void startTraining(int trainingType){
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, trainingType);
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, mSetId);
        startActivity(intent);
    }

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
        Snackbar.make(findViewById(R.id.coordinator_setact), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }



    public void resetSetProgress(){

        View.OnClickListener snackListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDbAdapter.resetSetProgress(mSetId);
                mRecyclerAdapter.changeCursor(mDbAdapter.fetchWordsBySetId(mSetId));
            }
        };

        Snackbar.make(findViewById(R.id.coordinator_setact), R.string.reset_progress_question, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reset, snackListener).show();
    }*/
}