package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by jrsao on 2/24/2015.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_ROUND_TYPE = "pref_round_type";
    public static final String KEY_ROUND_TIME = "pref_round_time_value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("id", 0);

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }

    public static boolean isListPreference(String key) {
        String h = key;
        return KEY_ROUND_TYPE.equals(key) ||
               KEY_ROUND_TIME.equals(key);
    }
}
