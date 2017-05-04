package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.Training;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

public class TrainingActivity extends AppCompatActivity implements TrainingFragment.ResultReceiver{

    public static final String EXTRA_TRAINING_TYPE = "training_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        int training_type = getIntent().getIntExtra(EXTRA_TRAINING_TYPE, TrainingFabric.WORD_TRANSLATION);

        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayShowHomeEnabled(true);
            bar.setHomeButtonEnabled(true);
        }

        TrainingFragment fragment = TrainingFragment.getWordTrainingFragment(training_type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_training, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void showResults(Results results) {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_RESULTS, results);
        setResult(RESULT_OK, intent);
        finish();
    }
}
