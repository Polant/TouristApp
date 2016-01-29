package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.polant.touristapp.Constants;
import com.polant.touristapp.adapter.recycler.SearchMultiTypesAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IRecyclerFragment;
import com.polant.touristapp.interfaces.ISearchableFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;

/**
 * Created by Антон on 29.01.2016.
 */
public class SearchFragment extends Fragment implements IRecyclerFragment, ISearchableFragment {


    protected Activity mActivity;

    protected SearchMultiTypesAdapter mAdapter;

    protected Database db;

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

        getDatabase();
        getDataFromArguments(getArguments());
    }

    protected void getDatabase() {
        db = ((IWorkWithDatabaseActivity) mActivity).getDatabase();
    }

    protected void getDataFromArguments(Bundle args) {
        if (args != null) {
            mUserId = args.getInt(Constants.USER_ID);
        }
    }

    @Override
    public void notifyRecyclerView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void search(String match) {
//        mAdapter.changeItems();
        notifyRecyclerView();
    }
}
