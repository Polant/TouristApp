package com.polant.touristapp.fragment.base.recycler.cursor;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.polant.touristapp.adapter.base.CursorRecyclerViewMultiAdapter;
import com.polant.touristapp.fragment.base.recycler.BaseRecyclerFragment;

import java.util.List;

/**
 * Базовый фрагмент для всех фрагментов, содержащих RecyclerView с множественным выбором
 * и работающими с БД через Cursor-ы.
 */
public abstract class BaseRecyclerMultiChoiceFragment extends BaseRecyclerFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    protected CursorRecyclerViewMultiAdapter mAdapter;


    protected void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
    }

    public long[] getSelectedItemsIdsArray() {
        long[] result = new long[mAdapter.getSelectedItemCount()];
        List<Long> list = mAdapter.getSelectedItemsIds();
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    @Override
    public void notifyRecyclerView() {
        getLoaderManager().restartLoader(0, null, this);
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
