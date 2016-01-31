package com.polant.touristapp.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.polant.touristapp.Constants;
import com.polant.touristapp.adapter.base.RecyclerClickListener;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IRecyclerFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;

/**
 * Created by Антон on 31.01.2016.
 */
public abstract class BaseRecyclerFragment extends Fragment
        implements IRecyclerFragment, RecyclerClickListener {

    protected Activity mActivity;

    protected int mUserId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IWorkWithDatabaseActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT IWorkWithDatabaseActivity");
        }
        mActivity = (Activity) context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDataFromArguments(getArguments());
    }

    protected void getDataFromArguments(Bundle args) {
        if (args != null) {
            mUserId = args.getInt(Constants.USER_ID);
        }
    }

    protected Database getDatabase() {
        return ((IWorkWithDatabaseActivity) mActivity).getDatabase();
    }
}
