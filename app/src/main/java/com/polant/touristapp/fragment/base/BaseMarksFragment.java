package com.polant.touristapp.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksActivity;
import com.polant.touristapp.adapter.recycler.MarksCursorMultiAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IMultiChoiceRecyclerFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;

import java.util.List;

/**
 * Базовый фрагмент, содержащий RecyclerView для вывода меток.
 */
public abstract class BaseMarksFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IMultiChoiceRecyclerFragment,
                   MarksCursorMultiAdapter.MarkViewHolder.ClickListener {

    protected Activity mActivity;

    protected MarksCursorMultiAdapter mAdapter;

    protected Database db;

    protected int mUserId;

    protected ActionMode mActionMode;
    protected ActionMode.Callback mActionModeCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IWorkWithDatabaseActivity) || !(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT IWorkWithDatabaseActivity and will be" +
                    " a child of AppCompatActivity");
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

    protected abstract void startActionMode();

    protected void refreshActionMode(int selectedCount) {
        if (mActionMode != null) {
            if (selectedCount == 0) {
                mActionMode.finish();
            } else {
                mActionMode.setTitle(String.valueOf(getString(R.string.overlay_text) + " " + selectedCount));
                mActionMode.invalidate();
            }
        }
    }

    protected void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();
        refreshActionMode(count);
    }


    @Override
    public void notifyRecyclerView() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public long[] getSelectedItemsIdsArray() {
        long[] result = new long[mAdapter.getSelectedItemCount()];
        List<Long> list = mAdapter.getSelectedItemsIds();
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity, null, null, null, null, null){
            @Override
            public Cursor loadInBackground() {
                return db.selectMarksAndPhotosCountCursor(mUserId);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
