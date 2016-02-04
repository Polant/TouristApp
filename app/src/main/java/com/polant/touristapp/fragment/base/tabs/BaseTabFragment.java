package com.polant.touristapp.fragment.base.tabs;

import android.support.v4.app.Fragment;

/**
 * Created by Антон on 04.02.2016.
 */
public abstract class BaseTabFragment extends Fragment {

    protected String tabTitle;

    protected String contentText;

    protected int contentDrawable;

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String title) {
        this.tabTitle = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setContentDrawable(int contentDrawable) {
        this.contentDrawable = contentDrawable;
    }
}