package com.polant.touristapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.polant.touristapp.R;

public class MarksActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_marks_recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

    }

}
