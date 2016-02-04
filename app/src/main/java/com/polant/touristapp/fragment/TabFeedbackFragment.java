package com.polant.touristapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.polant.touristapp.R;
import com.polant.touristapp.fragment.base.tabs.BaseTabFragment;

/**
 * Created by Антон on 04.02.2016.
 */
public class TabFeedbackFragment extends BaseTabFragment {

    private static final int LAYOUT = R.layout.fragment_feedback;

    private String appVersion;

    private String feedbackEmailLink;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT, container, false);

        ImageView contentHelp = (ImageView) view.findViewById(R.id.image_feedback_content);
        contentHelp.setImageResource(contentDrawable);

        TextView appVersionTextView = (TextView)view.findViewById(R.id.feedback_app_version);
        appVersionTextView.setText(appVersion);

        TextView emailTextView = (TextView)view.findViewById(R.id.feedback_email_link);
        emailTextView.setText(feedbackEmailLink);

        TextView contentTextView = (TextView)view.findViewById(R.id.feedback_text);
        contentTextView.setText(contentText);

        return view;
    }

    public static TabFeedbackFragment getInstance(String tabTitle, String contentText, int drawableRes,
                                              String appVersion, String email){
        Bundle args = new Bundle();
        TabFeedbackFragment fragment = new TabFeedbackFragment();

        fragment.setArguments(args);
        fragment.setTabTitle(tabTitle);
        fragment.setContentText(contentText);
        fragment.setContentDrawable(drawableRes);
        fragment.setAppVersion(appVersion);
        fragment.setFeedbackEmailLink(email);

        return fragment;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setFeedbackEmailLink(String feedbackEmailLink) {
        this.feedbackEmailLink = feedbackEmailLink;
    }
}
