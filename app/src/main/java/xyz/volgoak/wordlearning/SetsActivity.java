package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;

import xyz.volgoak.wordlearning.training_utils.Training;

public class SetsActivity extends NavigationActivity implements FragmentListener, WordSetsFragment.SetsFragmentListener{

    private WordSetsFragment mSetsFragment;
    private SingleSetFragment mSingleSetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);
        super.onCreateNavigationDrawer();

        mSetsFragment =(WordSetsFragment) getSupportFragmentManager().findFragmentById(R.id.container_master_sets_activity);
        if(mSetsFragment == null){
            mSetsFragment = new WordSetsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_master_sets_activity, mSetsFragment)
                    .commit();
        }

        mSingleSetFragment = (SingleSetFragment)getSupportFragmentManager().findFragmentById(R.id.container_detail_sets_activity);
        if(mSingleSetFragment == null && findViewById(R.id.container_detail_sets_activity) != null){
            // TODO: 17.06.2017 change test id to real
            mSingleSetFragment = SingleSetFragment.newInstance(1, false);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detail_sets_activity, mSingleSetFragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void startSet(long setId) {
        if(findViewById(R.id.container_detail_sets_activity ) != null){
            mSingleSetFragment = SingleSetFragment.newInstance(setId, false);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detail_sets_activity, mSingleSetFragment)
                    .commit();
        }else {
            Intent intent = new Intent(this, SingleSetActivity.class);
            intent.putExtra(SingleSetActivity.ID_EXTRA, setId);
            startActivity(intent);
        }
    }

    @Override
    public void startTraining(int type) {
        startTraining(type, -1);
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
    public void setActionBarTitle(String title) {
        //do nothing. Just part of interface
    }
}
