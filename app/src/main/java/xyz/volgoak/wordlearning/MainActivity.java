package xyz.volgoak.wordlearning;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentListener{

    public static final String SAVED_FRAGMENT_TAG = "fragment_tag";
    private Fragment mMyFragment;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        if(savedInstanceState != null){
            mMyFragment = getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAGMENT_TAG);
        }else mMyFragment = new StartFragment();

        startFragment(mMyFragment);
    }

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
    public boolean onNavigationItemSelected(MenuItem item){
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.navigation_menu_trans_word :
                startTrainingTWFragment();
                break;
            case R.id.navigation_menu_word_trans :
                startTrainingWTFragment();
                break;
            case R.id.navigation_menu_redactor :
                startRedactorFragment();
                break;
            case R.id.navigation_menu_exit :
                System.exit(0);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    // TODO: 31.03.2017 replace all of this with one method startFragment

    public void startRedactorFragment(){
        RedactorFragment redactorFragment = new RedactorFragment();
        startFragment(redactorFragment);
    }

    public void startTrainingWTFragment(){
        TrainingFragment trainingFragment = TrainingFragment.getWordTrainingFragment(TrainingFabric.WORD_TRANSLATION);
        startFragment(trainingFragment);
    }

    public void startTrainingTWFragment(){
        TrainingFragment trainingFragment = TrainingFragment.getWordTrainingFragment(TrainingFabric.TRANSLATION_WORD);
        startFragment(trainingFragment);
    }

    public void startResultsFragment(int correctAnswers, int wrongAnswers){
        ResultsFragment resultsFragment = ResultsFragment.getResultFragment(correctAnswers, wrongAnswers, this);
        startFragment(resultsFragment);
    }

    public void startFragment(Fragment fragment){
        mMyFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, SAVED_FRAGMENT_TAG, mMyFragment);
    }
}
