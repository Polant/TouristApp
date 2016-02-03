package com.polant.touristapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polant.touristapp.R;

/**
 * Created by Антон on 03.02.2016.
 */
public class TabHelpFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_help;

    private Context context;

    private View view;

    private String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        TextView textViewTitle = (TextView)view.findViewById(R.id.textViewHelpFragmentText);
        textViewTitle.setText(title);

        return view;
    }

    public static TabHelpFragment getInstance(Context context, int titleRes){
        Bundle args = new Bundle();
        TabHelpFragment fragment = new TabHelpFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(titleRes));

        return fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
