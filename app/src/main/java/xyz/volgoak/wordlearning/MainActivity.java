package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

import static android.speech.RecognizerIntent.EXTRA_RESULTS;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentListener{

    public static final int TRAINING_REQUEST = 123;
    public static final int TRAINING_FINISHED = 234;
    public static final String EXTRA_RESULTS = "extra_results";
    public static final String TAG = "MainActivity";

    private boolean mReturningWithResult;
    private Results mTrainingResult;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        startHomeFragment();
    }

    //load default words at first launching
    /*private void checkFirstLaunch(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstLaunch = prefs.getBoolean(getString(R.string.first_launch), true);
        if(firstLaunch){
            WordsDbAdapter adapter = new WordsDbAdapter(this);
            adapter.insertDefaultDictionary();
            prefs.edit().putBoolean(getString(R.string.first_launch), false).apply();
        }
    }*/

    @Override
    protected void onStart(){
        super.onStart();
        /*StartFragment startFragment = new StartFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, startFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();*/
    }

    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mReturningWithResult){
            startResultsFragment(mTrainingResult);
        }
        mReturningWithResult = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK && requestCode == TRAINING_REQUEST ){
            mTrainingResult =(Results) data.getSerializableExtra(EXTRA_RESULTS);
            mReturningWithResult = true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.navigation_menu_to_home :
                startHomeFragment();
                break;
            case R.id.navigation_menu_trans_word :
                startTraining(TrainingFabric.TRANSLATION_WORD);
                break;
            case R.id.navigation_menu_word_trans :
                startTraining(TrainingFabric.WORD_TRANSLATION);
                break;
            case R.id.navigation_menu_redactor :
                startRedactorFragment();
                break;
            case R.id.navigation_menu_sets :
                startSetsFragment();
                break;
            case R.id.navigation_menu_exit :
                System.exit(0);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    // TODO: 31.03.2017 replace all of this with one method startFragment
    // TODO: 05.05.2017 don't add start frag to the backstack

    public void startHomeFragment(){
        StartFragment fragment = new StartFragment();
        startFragment(fragment);
    }

    public void startRedactorFragment(){
        RedactorFragment redactorFragment = new RedactorFragment();
        startFragment(redactorFragment);
    }

    public void startTrainingWT(){
        TrainingFragment trainingFragment = TrainingFragment.getWordTrainingFragment(TrainingFabric.WORD_TRANSLATION);
        startFragment(trainingFragment);

        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, TrainingFabric.WORD_TRANSLATION);
        startActivity(intent);
    }

    @Override
    public void startTraining(int type){
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type);
        startActivityForResult(intent, TRAINING_REQUEST);
    }

    @Override
    public void startSetsFragment() {
        WordSetsFragment wordSetsFragment = new WordSetsFragment();
        startFragment(wordSetsFragment);
    }

    @Override
    public void startResultsFragment(Results results){
        ResultsFragment resultsFragment = ResultsFragment.getResultFragment(results);
        startFragment(resultsFragment);
    }

    public void startFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setActionBarTitle(String title){
        if(getSupportActionBar() != null)
        getSupportActionBar().setTitle(title);
    }


}
