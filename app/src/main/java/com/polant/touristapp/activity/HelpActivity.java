package com.polant.touristapp.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.polant.touristapp.R;
import com.polant.touristapp.adapter.tabs.TabsHelpFragmentAdapter;

/**
 * Created by Антон on 03.02.2016.
 */
public class HelpActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_help;

    private TabsHelpFragmentAdapter mAdapter;

    private TextView mHelpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mHelpText = (TextView)findViewById(R.id.helpTextView);
        initToolbar();
        initTabLayout();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_help);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initTabLayout() {
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);

        mAdapter = new TabsHelpFragmentAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new PagerChangeListener());

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayoutHelp);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private class PagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
