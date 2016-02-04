package com.polant.touristapp.adapter.tabs;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.polant.touristapp.R;
import com.polant.touristapp.fragment.TabFeedbackFragment;
import com.polant.touristapp.fragment.TabHelpFragment;
import com.polant.touristapp.fragment.base.tabs.BaseTabFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Антон on 03.02.2016.
 */
public class TabsHelpFragmentAdapter extends FragmentPagerAdapter {

    private Context context;

    private Map<Integer, BaseTabFragment> tabs;

    public TabsHelpFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
        initTabs();
    }

    private void initTabs() {
        tabs = new HashMap<>();
        tabs.put(0, TabHelpFragment.getInstance(context.getString(R.string.tab_selected_photo_title),
                context.getString(R.string.help_photo_text),
                R.drawable.help_selected_photo));
        tabs.put(1, TabHelpFragment.getInstance(context.getString(R.string.tab_map_title),
                context.getString(R.string.help_map_text),
                R.drawable.help_map));
        tabs.put(2, TabHelpFragment.getInstance(context.getString(R.string.tab_marks_title),
                context.getString(R.string.help_title_text),
                R.drawable.help_marks));
        tabs.put(3, TabHelpFragment.getInstance(context.getString(R.string.tab_search_title),
                context.getString(R.string.help_search_text),
                R.drawable.help_search));
        tabs.put(4, TabHelpFragment.getInstance(context.getString(R.string.tab_settings_title),
                context.getString(R.string.help_settings_text),
                R.drawable.help_settings));
        tabs.put(5, TabFeedbackFragment.getInstance(context.getString(R.string.tab_feedback_title),
                context.getString(R.string.feedback_text),
                R.mipmap.ic_launcher,
                context.getString(R.string.feedback_app_version),
                context.getString(R.string.feedback_email)));
    }

    @Override
    public BaseTabFragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTabTitle();
    }
}
