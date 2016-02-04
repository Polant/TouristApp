package com.polant.touristapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.polant.touristapp.R;
import com.polant.touristapp.fragment.base.tabs.BaseTabFragment;

/**
 * Created by Антон on 03.02.2016.
 */
public class TabHelpFragment extends BaseTabFragment {

    private static final int LAYOUT = R.layout.fragment_help;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT, container, false);

        ImageView contentHelp = (ImageView) view.findViewById(R.id.help_item_content);
        contentHelp.setImageResource(contentDrawable);

        return view;
    }

    public static TabHelpFragment getInstance(String tabTitle, String helpText, int drawableRes){
        Bundle args = new Bundle();
        TabHelpFragment fragment = new TabHelpFragment();

        fragment.setArguments(args);
        fragment.setTabTitle(tabTitle);
        fragment.setContentText(helpText);
        fragment.setContentDrawable(drawableRes);
        return fragment;
    }
}
