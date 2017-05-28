package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.databinding.ActivitySetBinding;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;
import xyz.volgoak.wordlearning.utils.DictionaryRecyclerAdapter;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class SetActivity extends AppCompatActivity {

    public static final String TAG = "SetActivity";
    public static final String ID_EXTRA = "id_extra";

    private ActivitySetBinding mBinding;

    private WordsDbAdapter mDbAdapter;
    private DictionaryRecyclerAdapter mRecyclerAdapter;
    private long mSetId;
    private boolean mSetInDictionary;
    private String mSetName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_set);
        setSupportActionBar(mBinding.setToolbar);

        mSetId = getIntent().getLongExtra(ID_EXTRA, -1);
        mDbAdapter = new WordsDbAdapter();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);

        loadSetInformation();

        recyclerOperations();

    }

    private void loadSetInformation(){
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

        // TODO: 11.05.2017 add default dravable
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .centerCrop()
                .crossFade().error(R.drawable.button_back)
                .into(mBinding.setIvTitle);


        int setStatus = setCursor.getInt(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_STATUS));
        mSetInDictionary = setStatus == DatabaseContract.Sets.IN_DICTIONARY;

        manageTrainingStatusFab();

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



    private void manageTrainingStatusFab(){
        mBinding.setResetFab.setVisibility(mSetInDictionary ? View.VISIBLE : View.GONE);
        int addDrawableId = mSetInDictionary ? R.drawable.ic_remove_white_24dp : R.drawable.ic_add_white_24dp;
        mBinding.setAddFab.setImageDrawable(ContextCompat.getDrawable(this, addDrawableId));
    }

    private void manageTrainingStatusMenu(){

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addItem = menu.findItem(R.id.item_add_setact);
        addItem.setTitle(mSetInDictionary ? R.string.remove_from_dictionary : R.string.add_to_dictionary);

        MenuItem resetItem = menu.findItem(R.id.item_reset_setact);
        resetItem.setVisible(mSetInDictionary);

        return super.onPrepareOptionsMenu(menu);
    }

    private void recyclerOperations(){
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
        int newStatus = mSetInDictionary ? DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
        mSetInDictionary = !mSetInDictionary;

        mDbAdapter.changeSetStatus(mSetId, newStatus);
        manageTrainingStatusFab();
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
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        manageTrainingStatusMenu();
        super.onStart();
    }

    @Override
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
    }
}
