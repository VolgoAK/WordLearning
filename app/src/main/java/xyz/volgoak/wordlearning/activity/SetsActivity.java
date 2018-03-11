package xyz.volgoak.wordlearning.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.fragment.ContainerFragment;
import xyz.volgoak.wordlearning.fragment.SingleSetFragment;
import xyz.volgoak.wordlearning.fragment.WordCardsFragment;
import xyz.volgoak.wordlearning.fragment.WordSetsFragment;

public class SetsActivity extends AppCompatActivity implements FragmentListener, WordSetsFragment.SetsFragmentListener {

    private WordSetsFragment mSetsFragment;
    private SingleSetFragment mSingleSetFragment;

    private boolean isMultiFrag;
    private long mSelectedSetId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        isMultiFrag = findViewById(R.id.container_detail_sets_activity) != null;

        // TODO: 3/11/18 cast exception here
        mSetsFragment = (WordSetsFragment) getSupportFragmentManager().findFragmentById(R.id.container_master_sets_activity);
        if (mSetsFragment == null) {
            mSetsFragment = WordSetsFragment.newInstance(isMultiFrag);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_master_sets_activity, mSetsFragment)
                    .commit();
        }

        mSingleSetFragment = (SingleSetFragment) getSupportFragmentManager().findFragmentById(R.id.container_detail_sets_activity);
        if (mSingleSetFragment == null && isMultiFrag) {
            // TODO: 17.06.2017 change test id to real
            mSingleSetFragment = SingleSetFragment.newInstance(1, false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_detail_sets_activity, mSingleSetFragment)
                    .commit();
        }
    }

    @Override
    public void startSet(long setId, View shared) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean singleMode = findViewById(R.id.container_detail_sets_activity) == null;
        int container = singleMode ? R.id.container_master_sets_activity : R.id.container_master_sets_activity;

        mSingleSetFragment = SingleSetFragment.newInstance(setId, singleMode);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = ViewCompat.getTransitionName(shared);
            mSingleSetFragment.getArguments().putString(SingleSetFragment.EXTRA_TRANSITION_NAME, transitionName);
            transaction.addSharedElement(shared, transitionName);
        }

        transaction.replace(container, mSingleSetFragment)
                .addToBackStack(null)
                .commit();

        mSelectedSetId = setId;
    }

    @Override
    public void startTraining(int type) {
        startTraining(type, mSelectedSetId);
    }

    @Override
    public void startTraining(int type, long setId) {
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type);
        startActivity(intent);
    }

    @Override
    public void startDictionary() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.START_DICTIONARY);
        startActivity(intent);
    }

    @Override
    public void startSets() {
        //do nothing sets already started
    }

    @Override
    public void startCards(int startPosition) {
        boolean singleMode = findViewById(R.id.container_detail_sets_activity) == null;
        int container = singleMode ? R.id.container_master_sets_activity : R.id.container_master_sets_activity;
        ContainerFragment fragment = ContainerFragment.newInstance(startPosition);
        getSupportFragmentManager().beginTransaction()
                .replace(container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void setActionBarTitle(String title) {
        //do nothing. Just part of interface
    }

    @Override
    public void selectTraining() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.SELECT_TRAINING);
        startActivity(intent);
    }
}
