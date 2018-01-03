package xyz.volgoak.wordlearning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.fragment.SingleSetFragment;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class SingleSetActivity extends AppCompatActivity implements FragmentListener {

    public static final String TAG = "SingleSetActivity";
    public static final String ID_EXTRA = "id_extra";

    private SingleSetFragment mSetFragment;
    private long mSetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_single_set);

        FirebaseAuth.getInstance().signInAnonymously();

        mSetId = getIntent().getLongExtra(ID_EXTRA, -1);

        mSetFragment =(SingleSetFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);

        findViewById(android.R.id.content).setBackgroundResource(R.drawable.background);

        if(mSetFragment == null){
            mSetFragment = SingleSetFragment.newInstance(mSetId, true);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, mSetFragment)
                    .commit();
        }
    }

    @Override
    public void startTraining(int type) {
        startTraining(type, -1);
    }

    @Override
    public void startTraining(int type, long setId) {
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type);
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId);
        startActivity(intent);
    }

    @Override
    public void startDictionary() {

    }

    @Override
    public void startSets() {

    }

    @Override
    public void setActionBarTitle(String title) {

    }
}
