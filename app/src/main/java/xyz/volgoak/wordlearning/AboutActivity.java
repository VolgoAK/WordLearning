package xyz.volgoak.wordlearning;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.artitk.licensefragment.model.License;
import com.artitk.licensefragment.model.LicenseType;
import com.artitk.licensefragment.support.v4.RecyclerViewLicenseFragment;
import java.util.ArrayList;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class AboutActivity extends AppCompatActivity {

    Fragment mLicenseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about);
        toolbar.setTitle(R.string.about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_about);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_about);

        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Fragment buildLicenseFragment(){
        if(mLicenseFragment == null){
            RecyclerViewLicenseFragment fragment = RecyclerViewLicenseFragment.newInstance();
            ArrayList<License> licenses = new ArrayList<>();
            licenses.add(new License(this, "Support Design", LicenseType.APACHE_LICENSE_20, "2016", "Android Open Source Project"));
            licenses.add(new License(this, "Support ConstraintLayout", LicenseType.APACHE_LICENSE_20, "2015", "Android Open Source Project"));
            licenses.add(new License(this, "Support CardView", LicenseType.APACHE_LICENSE_20, "2015", "Android Open Source Project"));
            licenses.add(new License(this, "CursorRecyclerAdapter", LicenseType.MIT_LICENSE, "2014", "Matthieu Harlé"));
            licenses.add(new License(this, "FirebaseStorage", LicenseType.APACHE_LICENSE_20, "2016", "Google Inc"));
            licenses.add(new License(this, "FirebaseAuth", LicenseType.APACHE_LICENSE_20, "2016", "Google Inc"));
            licenses.add(new License(this, "Firebase UI Storage", LicenseType.APACHE_LICENSE_20, "2016", "Google Inc"));
            licenses.add(new License(this, "Google PlayServices GCM", LicenseType.APACHE_LICENSE_20, "2015", "Google Inc"));
            licenses.add(new License(this, "AppCompat v7", LicenseType.APACHE_LICENSE_20, "2015", "Android Open Source Project"));
            licenses.add(new License(this, "Support RecyclerView v7", LicenseType.APACHE_LICENSE_20, "2015", "Android Open Source Project"));
            licenses.add(new License(this, "Support v4", LicenseType.APACHE_LICENSE_20, "2014", "Android Open Source Project"));
            licenses.add(new License(this, "Android-RoundCornerProgressBar", LicenseType.APACHE_LICENSE_20, "2015", "Akexorcist"));
            licenses.add(new License(this, "CircleImageView", LicenseType.APACHE_LICENSE_20, "2014 - 2017", "Henning Dodenhof"));
            licenses.add(new License(this, "Glide", LicenseType.APACHE_LICENSE_20, "2016", "Bump Technologies"));

            fragment.addCustomLicense(licenses);
            mLicenseFragment = fragment;
        }

        return mLicenseFragment;
    }


    private class TabAdapter extends FragmentPagerAdapter{

        public static final int ITEM_COUNT = 2;


        TabAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :

                    return new AboutAppFragment();
                case 1 :
                    return buildLicenseFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return getString(R.string.about);
                case 1 :
                    return getString(R.string.open_source);
                default: throw new IndexOutOfBoundsException("fragment position out of range");
            }
        }


    }
}
