package xyz.volgoak.wordlearning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.admob.AdsManager;
import xyz.volgoak.wordlearning.admob.Banner;
import xyz.volgoak.wordlearning.fragment.BoolTrainingFragment;
import xyz.volgoak.wordlearning.fragment.TimerFragment;
import xyz.volgoak.wordlearning.fragment.TrainingFragment;
import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;


public class TrainingActivity extends AppCompatActivity implements TrainingFragment.ResultReceiver, TimerFragment.TimerListener {

    public static final String EXTRA_TRAINING_TYPE = "training_type";
    public static final String EXTRA_SET_ID = "set_id";

    private int trainingType;
    private long setId;

    private Banner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        trainingType = getIntent().getIntExtra(EXTRA_TRAINING_TYPE, TrainingFabric.WORD_TRANSLATION);
        setId = getIntent().getLongExtra(EXTRA_SET_ID, -1);

        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayShowHomeEnabled(true);
            bar.setHomeButtonEnabled(true);
        }

        if(savedInstanceState == null) {
            if(trainingType == TrainingFabric.BOOL_TRAINING) {
                Fragment trainingFragment = new TimerFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_training, trainingFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
            } else onTimerFinished();
        }

        if(AdsManager.INSTANCE.getInitialized()) {
            LinearLayout bannerContainer = findViewById(R.id.llBannerContainerTraining);
            banner = new Banner(this);
            banner.loadAdRequest();
            banner.setTargetView(bannerContainer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(banner != null) {
            banner.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(banner != null) {
            banner.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(banner != null) {
            banner.onDestroy();
        }
    }

    @Override
    public void showResults(Results results) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.Companion.getEXTRA_TRAINING_RESULTS(), results);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimerFinished() {
        Fragment trainingFragment;
        if(trainingType == TrainingFabric.BOOL_TRAINING) {
            trainingFragment = BoolTrainingFragment.newInstance(setId);
        } else {
            trainingFragment = TrainingFragment.getWordTrainingFragment(trainingType, setId);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_training, trainingFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
}
