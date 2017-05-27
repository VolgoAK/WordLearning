package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.volgoak.wordlearning.training_utils.Results;

public class ResultActivity extends AppCompatActivity implements FragmentListener{

    public static final String EXTRA_TRAINING_RESULTS = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Results results = (Results) getIntent().getSerializableExtra(EXTRA_TRAINING_RESULTS);
        Fragment fragment;
        switch (results.resultType){
            case NO_WORDS :
                fragment = new NoWordsFragment();
                break;
            default :
                fragment = ResultsFragment.getResultFragment(results);
                break;
        }
        //ResultsFragment fragment = ResultsFragment.getResultFragment(results);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.result_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.commit();
    }

    @Override
    public void startTraining(int trainingType, long setId) {
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, trainingType);
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId);
        startActivity(intent);
        finish();
    }

    @Override
    public void startDictionary() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.START_DICTIONARY);
        startActivity(intent);

        finish();
    }

    @Override
    public void startTraining(int type) {
        startTraining(type, -1);
    }

    @Override
    public void startSets() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.START_SETS);
        startActivity(intent);

        finish();
    }

    @Override
    public void setActionBarTitle(String title) {

    }
}
