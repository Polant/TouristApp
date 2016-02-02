package com.polant.touristapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.polant.touristapp.R;
import com.polant.touristapp.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_settings;

    public static final String KEY_LOCATION_UPDATE_FREQUENCY = "key_location_update_frequency";
    public static final String KEY_LOCATION_UPDATE_MIN_DISTANCE = "key_location_update_min_distance";
//    public static final String KEY_GALLERY_AUTO_EXPORT = "key_gallery_auto_export";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initPreferenceFragment();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_settings);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initPreferenceFragment(){
        getFragmentManager().beginTransaction()
                .replace(R.id.container_settings, new SettingsFragment())
                .commit();
    }
}
