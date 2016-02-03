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
import com.polant.touristapp.fragment.TabHelpFragment;
import com.polant.touristapp.utils.image.DeviceDimensionsHelper;

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
        mAdapter = new TabsHelpFragmentAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);

        PagerChangeListener listener = new PagerChangeListener();

        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(listener);

        listener.onPageSelected(0);//Чтобы установить текст помощи для первой вкладки.

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayoutHelp);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private class PagerChangeListener implements ViewPager.OnPageChangeListener {

        private int deviceWidth = DeviceDimensionsHelper.getDisplayWidth(HelpActivity.this);

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            float percentOffset = Math.abs(((float)positionOffsetPixels) / deviceWidth);
            //TODO: отладить этот участок кода.
            if (percentOffset < 0.5){
                mHelpText.setAlpha(1 - percentOffset);
            }
            else{
                mHelpText.setAlpha(percentOffset);

                String text;
                if (positionOffsetPixels < -20 ){
                    text = mAdapter.getItem(position - 1).getHelpText();
                }
                else if (positionOffsetPixels > 20){
                    text = mAdapter.getItem(position + 1).getHelpText();
                }
                else{
                    text = mAdapter.getItem(position).getHelpText();
                }
                mHelpText.setText(text);
            }
        }

        @Override
        public void onPageSelected(int position) {
            TabHelpFragment selected = mAdapter.getItem(position);
            mHelpText.setText(selected.getHelpText());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
