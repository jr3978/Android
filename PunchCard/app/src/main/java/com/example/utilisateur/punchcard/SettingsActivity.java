package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by jrsao on 2/24/2015.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_CHECKING_FLING_LEFT_ACTION = "pref_checking_fling_to_left_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static boolean isListPreference(String key) {
        String h = key;
        return KEY_CHECKING_FLING_LEFT_ACTION.equals(key);
    }
}
