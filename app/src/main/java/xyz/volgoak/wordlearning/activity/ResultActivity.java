package xyz.volgoak.wordlearning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.fragment.NoWordsFragment;
import xyz.volgoak.wordlearning.fragment.ResultBoolFragment;
import xyz.volgoak.wordlearning.fragment.ResultsFragment;
import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class ResultActivity extends AppCompatActivity implements FragmentListener {

    public static final String EXTRA_TRAINING_RESULTS = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Results results = (Results) getIntent().getSerializableExtra(EXTRA_TRAINING_RESULTS);
        Fragment fragment;
        switch (results.resultType) {
            case NO_WORDS:
                fragment = new NoWordsFragment();
                break;
            default:
                switch (results.trainedType) {
                    case TrainingFabric.BOOL_TRAINING:
                        fragment = ResultBoolFragment.newInstance(results);
                        break;
                    default:
                        fragment = ResultsFragment.getResultFragment(results);
                        break;
                }
        }

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
        Intent intent = new Intent(this, SetsActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void setActionBarTitle(String title) {

    }

    @Override
    public void selectTraining() {

    }
}
