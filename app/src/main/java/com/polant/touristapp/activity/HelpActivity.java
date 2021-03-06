package com.polant.touristapp.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polant.touristapp.R;
import com.polant.touristapp.adapter.tabs.TabsHelpFragmentAdapter;
import com.polant.touristapp.fragment.base.tabs.BaseTabFragment;

/**
 * Created by Антон on 03.02.2016.
 */
public class HelpActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_help;

    private TabsHelpFragmentAdapter mAdapter;

    private TextView mHelpText;

    private ViewGroup mViewPagerRoot;
    private View mFooterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initViews();
        initToolbar();
        initTabLayout();
    }

    private void initViews() {
        mHelpText = (TextView)findViewById(R.id.helpTextView);
        mViewPagerRoot = (ViewGroup)findViewById(R.id.layout_view_pager_root);
        mFooterView = findViewById(R.id.layout_footer_help);
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

        private int footerHeight = 0;

        private final int positionFeedback = 5;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == positionFeedback - 1){
                if (footerHeight <= 0){
                    footerHeight = mFooterView.getHeight();
                }
                if (footerHeight > 0) {
                    int footerTranslation = (int) (2 * footerHeight * positionOffset);
                    if (footerTranslation > footerHeight) {
                        footerTranslation = footerHeight;
                    }

                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mFooterView.getLayoutParams();
                    if (lp.bottomMargin == -footerHeight) {
                        mFooterView.setVisibility(View.GONE);
                    }
                    if (footerTranslation < footerHeight && mFooterView.getVisibility() == View.GONE){
                        mFooterView.setVisibility(View.VISIBLE);
                    }
                    if (mFooterView.getVisibility() == View.VISIBLE){
                        lp.bottomMargin = -footerTranslation;
                        mFooterView.setLayoutParams(lp);
                        mViewPagerRoot.invalidate();
                    }

                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (position < positionFeedback) {
                BaseTabFragment selected = mAdapter.getItem(position);
                mHelpText.setText(selected.getContentText());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
