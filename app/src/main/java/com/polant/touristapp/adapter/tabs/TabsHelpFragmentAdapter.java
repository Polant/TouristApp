package com.polant.touristapp.adapter.tabs;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.polant.touristapp.R;
import com.polant.touristapp.fragment.TabHelpFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Антон on 03.02.2016.
 */
public class TabsHelpFragmentAdapter extends FragmentPagerAdapter {

    private Context context;

    private Map<Integer, TabHelpFragment> tabs;

    public TabsHelpFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
        initTabs();
    }

    private void initTabs() {
        tabs = new HashMap<>();
        tabs.put(0, TabHelpFragment.getInstance(context, R.string.tab_map_title));
        tabs.put(1, TabHelpFragment.getInstance(context, R.string.tab_marks_title));
        tabs.put(2, TabHelpFragment.getInstance(context, R.string.tab_search_title));
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }
}
