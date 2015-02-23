package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by jrsao on 2/22/2015.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_occupation, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(activity);

         getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updatePreferenceSummary(sharedPreferences, ActivitySetting.KEY_CHECKING_FLING_LEFT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (ActivitySetting.isListPreference(key))
            updatePreferenceSummary(sharedPreferences, key);
    }

    private void updatePreferenceSummary(SharedPreferences sharedPreferences, String key) {
        ListPreference p = (ListPreference) findPreference(getResources().getString(R.string.occ_params_reset_day_key));
        p.setSummary(p.getEntry());
    }
}
