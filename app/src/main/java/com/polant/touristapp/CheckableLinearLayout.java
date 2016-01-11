package com.polant.touristapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Антон on 11.01.2016.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable{

    private boolean isChecked;
    private List<Checkable> mCheckableViews;

    public CheckableLinearLayout(Context context) {
        super(context);
        init();
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        isChecked = false;
        mCheckableViews = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            findCheckableChildren(getChildAt(i));
        }
    }

    private void findCheckableChildren(View childAt) {
        if (childAt instanceof Checkable){
            mCheckableViews.add((Checkable)childAt);
        }
        if (childAt instanceof ViewGroup){
            final ViewGroup vg = (ViewGroup) childAt;
            final int childCount = vg.getChildCount();
            for (int i = 0; i < childCount; i++) {
                findCheckableChildren(vg.getChildAt(i));
            }
        }
    }

    //----------------------Реализация Checkable-------------------//

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        for (Checkable c : mCheckableViews){
            c.setChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        isChecked = !isChecked;
        for (Checkable c : mCheckableViews){
            c.toggle();
        }
    }
}
