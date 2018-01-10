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

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.fragment.RedactorFragment;
import xyz.volgoak.wordlearning.fragment.StartFragment;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class MainActivity extends NavigationActivity implements FragmentListener {

    public static final String TAG = "MainActivity";
    public static final String EXTRA_MODE = "extra_mode";
    public static final String START_DICTIONARY = "dictionary";

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
            }
        } else if (savedInstanceState == null)
            startHomeFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
            case R.id.item_about_main:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            /*case R.id.item_update_main :
                SetsLoader.checkForDbUpdate(this);
                return true;*/
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
            case R.id.navigation_menu_trans_word:
                startTraining(TrainingFabric.TRANSLATION_WORD);
                break;
            case R.id.navigation_menu_word_trans:
                startTraining(TrainingFabric.WORD_TRANSLATION);
                break;
            case R.id.navigation_menu_redactor:
                startDictionary();
                break;
            case R.id.navigation_menu_sets:
                startSets();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public void startHomeFragment() {
        StartFragment fragment = new StartFragment();
        startFragment(fragment, false);
    }

    public void startDictionary() {
        RedactorFragment redactorFragment = new RedactorFragment();
        startFragment(redactorFragment, true);
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

    public void startFragment(Fragment fragment, boolean addToBackStack) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
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
