package com.polant.touristapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.polant.touristapp.R;
import com.polant.touristapp.activity.SettingsActivity;

/**
 * Если использовать не фрагменты, а PreferenceActivity, то есть решение:
 * https://github.com/davcpas1234/MaterialSettings/blob/
 * master/app/src/main/java/uk/verscreative/materialsettings/SettingsExampleActivity.java
 *
 * Решение взято со http://stackoverflow.com/questions/26564400/creating-a-preference-screen-with-support-v21-toolbar
 *
 * Документация от Google:
 * http://developer.android.com/intl/es/guide/topics/ui/settings.html
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Preference changed = findPreference(key);

        if (changed instanceof ListPreference){
            changed.setSummary(((ListPreference)changed).getEntry());
        }
    }

    private void initPreferencesSummary(SharedPreferences sp){
        onSharedPreferenceChanged(sp, SettingsActivity.KEY_LOCATION_UPDATE_FREQUENCY);
        onSharedPreferenceChanged(sp, SettingsActivity.KEY_LOCATION_UPDATE_MIN_DISTANCE);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);

        initPreferencesSummary(sp);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
