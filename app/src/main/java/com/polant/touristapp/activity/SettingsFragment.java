package com.polant.touristapp.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.polant.touristapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
    }
}
