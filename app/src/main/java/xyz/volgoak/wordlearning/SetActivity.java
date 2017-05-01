package xyz.volgoak.wordlearning;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.databinding.ActivitySetBinding;
import xyz.volgoak.wordlearning.utils.DictionaryRecyclerAdapter;

public class SetActivity extends AppCompatActivity {

    public static final String TAG = "SetActivity";
    public static final String ID_EXTRA = "id_extra";

    private ActivitySetBinding mBinding;

    private WordsDbAdapter mDbAdapter;
    private DictionaryRecyclerAdapter mRecyclerAdapter;
    private long mSetId;
    private boolean mSetInDictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_set);
        setSupportActionBar(mBinding.toolbar);

        mSetId = getIntent().getLongExtra(ID_EXTRA, -1);
        mDbAdapter = new WordsDbAdapter(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);

        prepareSetInformation();

        //inflate recyclerView
        recyclerOperations();



    }

    private void prepareSetInformation(){
        Cursor setCursor = mDbAdapter.fetchSetById(mSetId);
        setCursor.moveToFirst();

        String setName = setCursor.getString(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
        mBinding.tvToolbarTitleSetact.setText(getString(R.string.set_activity_title, setName));

        int wordsInSet = setCursor.getInt(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NUM_OF_WORDS));
        mBinding.tvWordsCountSetact.setText(getString(R.string.words_in_set, wordsInSet));

        int setStatus = setCursor.getInt(setCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_STATUS));
        mSetInDictionary = setStatus == DatabaseContract.Sets.IN_DICTIONARY;

        mBinding.tvSendToTrainSetact.setText(getString(R.string.reset_progress));

        manageTrainingStatus();

        mBinding.btChangeStatusSetact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newStatus = mSetInDictionary ? DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
                mSetInDictionary = !mSetInDictionary;

                mDbAdapter.changeSetStatus(mSetId, newStatus);
                manageTrainingStatus();
            }
        });

        mBinding.btToTrainingSetact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbAdapter.resetSetProgress(mSetId);
                mRecyclerAdapter.changeCursor(mDbAdapter.fetchWordsBySetId(mSetId));
            }
        });
    }

    private void manageTrainingStatus(){
        String setStatusString = mSetInDictionary ? getString(R.string.set_in_the_dictionary) : getString(R.string.set_out_of_dictionary);
        mBinding.tvSetstatusSetact.setText(setStatusString);
        String changeStatusButton = mSetInDictionary ? getString(R.string.remove) : getString(R.string.add_set);
        mBinding.btChangeStatusSetact.setText(changeStatusButton);

        mBinding.tvSendToTrainSetact.setVisibility(mSetInDictionary ? View.VISIBLE : View.GONE);
        mBinding.btToTrainingSetact.setVisibility(mSetInDictionary ? View.VISIBLE : View.GONE);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
