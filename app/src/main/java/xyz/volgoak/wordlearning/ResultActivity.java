package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.Training;

public class ResultActivity extends AppCompatActivity implements ResultsFragment.ResultFragmentLister{

    public static final String EXTRA_TRAINING_RESULTS = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Results results = (Results) getIntent().getSerializableExtra(EXTRA_TRAINING_RESULTS);
        ResultsFragment fragment = ResultsFragment.getResultFragment(results);
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
    public void toDictionary() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.START_DICTIONARY);
        startActivity(intent);

        finish();
    }
}
