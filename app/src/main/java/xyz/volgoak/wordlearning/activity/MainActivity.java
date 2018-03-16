package xyz.volgoak.wordlearning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import timber.log.Timber;
import xyz.volgoak.wordlearning.AppRater;
import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.fragment.DictionaryFragment;
import xyz.volgoak.wordlearning.fragment.StartFragment;
import xyz.volgoak.wordlearning.fragment.TrainingSelectFragment;
import xyz.volgoak.wordlearning.fragment.WordCardsFragment;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class MainActivity extends NavigationActivity implements FragmentListener {

    public static final String TAG = "MainActivity";
    public static final String EXTRA_MODE = "extra_mode";
    public static final String START_DICTIONARY = "dictionary";
    public static final String SELECT_TRAINING = "select_training";

    private boolean exitPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateNavigationDrawer();

        String extraTask = getIntent().getStringExtra(EXTRA_MODE);
        if (extraTask != null) {
            if (extraTask.equals(START_DICTIONARY)) {
                startDictionary();
            } else if (extraTask.equals(SELECT_TRAINING)) {
                selectTraining();
            }
        } else if (savedInstanceState == null)
            startHomeFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppRater.app_launched(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current != null && current instanceof StartFragment) {
            if (exitPressed) finish();
            else {
                exitPressed = true;
                Toast.makeText(this, R.string.press_exit_again, Toast.LENGTH_LONG).show();
                new Handler().postDelayed(() -> exitPressed = false, 2000);
            }
        } else super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.navigation_menu_to_home:
                startHomeFragment();
                break;
            case R.id.navigation_menu_training:
                selectTraining();
                break;
            case R.id.navigation_menu_redactor:
                startDictionary();
                break;
            case R.id.navigation_menu_sets:
                startSets();
                break;
            case R.id.navigation_menu_rate:
                AppRater.rateApp(this);
                break;
            case R.id.navigation_menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public void startHomeFragment() {
        StartFragment fragment = new StartFragment();
        startFragment(fragment);
    }

    public void startDictionary() {
        DictionaryFragment redactorFragment = new DictionaryFragment();
        startFragment(redactorFragment);
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
    public void startSets() {
        Intent intent = new Intent(this, SetsActivity.class);
        startActivity(intent);
    }

    @Override
    public void selectTraining() {
        TrainingSelectFragment fragment = new TrainingSelectFragment();
        startFragment(fragment);
    }

    @Override
    public void startCards(int startPosition) {
        WordCardsFragment fragment = WordCardsFragment.newInstance(startPosition);
        startFragment(fragment);
    }

    public void startFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        boolean addToBackStack = false;
        if (currentFragment != null) {
            addToBackStack = true;
            Class<?> klass = currentFragment.getClass();
            if (klass.isInstance(fragment)) return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack) ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }
}
