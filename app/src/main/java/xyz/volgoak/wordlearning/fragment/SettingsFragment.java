package xyz.volgoak.wordlearning.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import xyz.volgoak.wordlearning.AppRater;
import xyz.volgoak.wordlearning.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private PreferenceFragmentListener fragmentListener;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference osPref = findPreference(getString(R.string.preference_license_key));
        osPref.setOnPreferenceClickListener(preference -> {
            fragmentListener.showOpensourceLicense();
            return true;
        });

        Preference ratePref = findPreference(getString(R.string.preference_rate_key));
        ratePref.setOnPreferenceClickListener(preference -> {
            AppRater.rateApp(getContext());
            return true;
        });

        Preference contentPref = findPreference(getString(R.string.preference_content_key));
        contentPref.setOnPreferenceClickListener(preference -> {
            fragmentListener.showContentLicens();
            return true;
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PreferenceFragmentListener) {
            fragmentListener = (PreferenceFragmentListener) context;
        } else throw new RuntimeException(context + " must implement PreferenceFragmentListener");
    }

    public interface PreferenceFragmentListener {
        void showOpensourceLicense();

        void showContentLicens();
    }
}
