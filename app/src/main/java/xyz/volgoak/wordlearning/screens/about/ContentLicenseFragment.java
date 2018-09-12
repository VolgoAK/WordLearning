package xyz.volgoak.wordlearning.screens.about;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import xyz.volgoak.wordlearning.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentLicenseFragment extends PreferenceFragmentCompat {


    public ContentLicenseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.licenses_preference, rootKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen screen = getPreferenceScreen();
        String[] titles = getResources().getStringArray(R.array.content_license_title);
        String[] summary = getResources().getStringArray(R.array.content_license_summary);
        String[] link = getResources().getStringArray(R.array.content_license_link);


        for (int i = 0; i < titles.length; i++) {
            Preference preference = new Preference(getContext());
            preference.setTitle(titles[i]);
            preference.setSummary(summary[i]);
            preference.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(link[i])));
            screen.addPreference(preference);
        }
    }
}
