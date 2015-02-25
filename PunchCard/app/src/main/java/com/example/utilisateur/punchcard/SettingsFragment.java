package com.example.utilisateur.punchcard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by jrsao on 2/24/2015.
 */
public class SettingsFragment  extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private DataBaseHandler _db;
    private OccupationParameters _parameters;
    private int _occupationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _db = new DataBaseHandler(getActivity().getApplicationContext());

        _occupationId = getArguments().getInt("id");
        addPreferencesFromResource(R.xml.preferences_occupation);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_occupation, false);

        if (_occupationId != 0)
        {
            _parameters = _db.getParametersByOccupationId(_occupationId);

            // set les valeurs par defaults
            if (_parameters != null)
            {
                ListPreference roundTime = (ListPreference) findPreference(SettingsActivity.KEY_ROUND_TIME);
                ListPreference roundType = (ListPreference) findPreference(SettingsActivity.KEY_ROUND_TYPE);

                int indexRoundTime = roundTime.findIndexOfValue(String.valueOf(_parameters.getRoundMinuteValue()));
                int indexRoundType = roundType.findIndexOfValue(String.valueOf(_parameters.getRoundType().getValue()));

                String[] types = getResources().getStringArray(R.array.round_type);
                String[] minutes = getResources().getStringArray(R.array.rounding_time);

                roundTime.setSummary(roundTime.getEntries()[indexRoundTime]);
                roundType.setSummary(roundType.getEntries()[indexRoundType]);

                roundTime.setValue(new Integer(_parameters.getRoundMinuteValue()).toString());
                roundType.setValue(new Integer(_parameters.getRoundType().getValue()).toString());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (SettingsActivity.isListPreference(key))
            updatePreferenceSummary(sharedPreferences, key);
    }

    private void updatePreferenceSummary(SharedPreferences sharedPreferences, String key) {
        ListPreference p = (ListPreference) findPreference(key);
        p.setSummary(p.getEntry());

        String valueStr = p.getValue();
        int value = Integer.parseInt(valueStr);

        if (_parameters != null)
        {
            switch (key)
            {
                case "pref_round_type":
                    _parameters.setRoundType(OccupationParameters.RoundType.values()[value]);
                    break;

                case "pref_round_time_value":
                    _parameters.setRoundMinuteValue(value);
                    break;

            }


            _db.updateParameters(_parameters);
        }
    }
}
