package com.polant.touristapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.polant.touristapp.R;

/**
 * Created by Антон on 03.02.2016.
 */
public class TabHelpFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_help;

    private View view;

    private String title;

    private int contentDrawable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        ImageView contentHelp = (ImageView) view.findViewById(R.id.help_item_content);
        contentHelp.setImageResource(contentDrawable);

        return view;
    }

    public static TabHelpFragment getInstance(String title, int drawableRes){
        Bundle args = new Bundle();
        TabHelpFragment fragment = new TabHelpFragment();

        fragment.setArguments(args);
        fragment.setTitle(title);
        fragment.setContentDrawable(drawableRes);
        return fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContentDrawable(int contentDrawable) {
        this.contentDrawable = contentDrawable;
    }
}
