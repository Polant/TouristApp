package com.polant.touristapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.polant.touristapp.R;

public class SettingsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_settings);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initPreferenceFragment();
    }

    private void initPreferenceFragment(){
        getFragmentManager().beginTransaction()
                .replace(R.id.container_settings, new SettingsFragment())
                .commit();
    }
}
