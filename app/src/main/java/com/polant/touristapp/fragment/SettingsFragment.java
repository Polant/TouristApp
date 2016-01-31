package com.polant.touristapp.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.polant.touristapp.R;

/**
 * Если использовать не фрагменты, то есть решение:
 * https://github.com/davcpas1234/MaterialSettings/blob/
 * master/app/src/main/java/uk/verscreative/materialsettings/SettingsExampleActivity.java
 *
 * Решение взято со http://stackoverflow.com/questions/26564400/creating-a-preference-screen-with-support-v21-toolbar
 */
public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
