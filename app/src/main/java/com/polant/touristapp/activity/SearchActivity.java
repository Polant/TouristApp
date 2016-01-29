package com.polant.touristapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.polant.touristapp.R;

public class SearchActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);
    }
}
