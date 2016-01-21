package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.adapter.recycler.MarksCursorAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IListFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;

/**
 * Created by Антон on 21.01.2016.
 */
public class MarksRecyclerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IListFragment {

    private static int LAYOUT = R.layout.fragment_marks_recycler;

    private Activity mActivity;
    private View mView;

    private Database db;
    private MarksCursorAdapter adapter;

    private int mUserId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IWorkWithDatabaseActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT IWorkWithDatabaseActivity");
        }
        mActivity = (Activity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(LAYOUT, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mUserId = args.getInt(Constants.USER_ID);
        }
        //Получил уже открытую базу.
        db = ((IWorkWithDatabaseActivity) mActivity).getDatabase();

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerViewMarks);
        adapter = new MarksCursorAdapter(mActivity, null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void notifyList() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity, null, null, null, null, null){
            @Override
            public Cursor loadInBackground() {
                return db.selectAllMarksCursorByUserId(mUserId);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
