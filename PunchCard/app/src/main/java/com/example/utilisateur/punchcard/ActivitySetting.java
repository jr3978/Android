package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by jrsao on 2/22/2015.
 */
public class ActivitySetting extends Activity
{

    public static final String KEY_CHECKING_FLING_LEFT_ACTION = "occ_params_reset_day_key";

    public static boolean isListPreference(String key) {
        return KEY_CHECKING_FLING_LEFT_ACTION.equals(key);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
