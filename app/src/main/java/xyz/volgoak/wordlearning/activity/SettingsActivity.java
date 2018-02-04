package xyz.volgoak.wordlearning.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.artitk.licensefragment.model.License;
import com.artitk.licensefragment.model.LicenseType;
import com.artitk.licensefragment.support.v4.RecyclerViewLicenseFragment;

import java.util.ArrayList;
import java.util.Collections;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.fragment.ContentLicenseFragment;
import xyz.volgoak.wordlearning.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.PreferenceFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, new SettingsFragment());
            transaction.commit();
        }
    }

    @Override
    public void showOpensourceLicense() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, buildLicenseFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void showContentLicens() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new ContentLicenseFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Fragment buildLicenseFragment() {
        RecyclerViewLicenseFragment fragment = RecyclerViewLicenseFragment.newInstance();
        ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License(this, "Support Design", LicenseType.APACHE_LICENSE_20, "2016", "Android Open Source Project"));
        licenses.add(new License(this, "Support RecyclerView v7", LicenseType.APACHE_LICENSE_20, "2015", "Android Open Source Project"));
        licenses.add(new License(this, "Firebase", LicenseType.APACHE_LICENSE_20, "2016", "Google Inc"));
        licenses.add(new License(this, "FirebaseAuth", LicenseType.APACHE_LICENSE_20, "2016", "Google Inc"));
        licenses.add(new License(this, "Firebase UI Storage", LicenseType.APACHE_LICENSE_20, "2016", "Google Inc"));
        licenses.add(new License(this, "Gson", LicenseType.APACHE_LICENSE_20, "2008", "Google Inc"));
        licenses.add(new License(this, "Google PlayServices GCM", LicenseType.APACHE_LICENSE_20, "2015", "Google Inc"));
        licenses.add(new License(this, "AppCompat v7", LicenseType.APACHE_LICENSE_20, "2015", "Android Open Source Project"));
        licenses.add(new License(this, "Dagger 2", LicenseType.APACHE_LICENSE_20, "2012", "The Dagger Authors"));
        licenses.add(new License(this, "Support v4", LicenseType.APACHE_LICENSE_20, "2014", "Android Open Source Project"));
        licenses.add(new License(this, "Android-RoundCornerProgressBar", LicenseType.APACHE_LICENSE_20, "2015", "Akexorcist"));
        licenses.add(new License(this, "CircleImageView", LicenseType.APACHE_LICENSE_20, "2014 - 2017", "Henning Dodenhof"));
        licenses.add(new License(this, "Glide", LicenseType.APACHE_LICENSE_20, "2016", "Bump Technologies"));
        licenses.add(new License(this, "Architecture Components", LicenseType.APACHE_LICENSE_20, "2015", "Android Open Sourse Project"));
        licenses.add(new License(this, "PlaceHolderView", LicenseType.APACHE_LICENSE_20, "2016", "Janishar Ali Anwar"));
        licenses.add(new License(this, "Eventbus", LicenseType.APACHE_LICENSE_20, "2012-2017", "Markus Junginger - Greenrobot"));
        licenses.add(new License(this, "Zif4j", LicenseType.APACHE_LICENSE_20, "2013", "Srikanth Reddy Lingala"));

        Collections.shuffle(licenses);

        fragment.addCustomLicense(licenses);

        return fragment;
    }
}
