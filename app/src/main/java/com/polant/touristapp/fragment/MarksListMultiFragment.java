package com.polant.touristapp.fragment;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksMultiChoiceActivity;
import com.polant.touristapp.adapter.recycler.MarksCursorMultiAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IMultiChoiceListFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Фрагмент, содержащий RecyclerView для вывода меток.
 */
public class MarksListMultiFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IMultiChoiceListFragment,
                   MarksCursorMultiAdapter.MarkViewHolder.ClickListener {

    private static final int LAYOUT = R.layout.fragment_marks_recycler_multi_choice;

    private View view;
    private Activity activity;

    private MarksCursorMultiAdapter mAdapter;

    private Database db;

    private int userId;
    private List<Long> inputMarks;

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IWorkWithDatabaseActivity) || !(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT IWorkWithDatabaseActivity and will be" +
                    " a child of AppCompatActivity");
        }
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            userId = args.getInt(Constants.USER_ID);
            long[] checkedIds = args.getLongArray(MarksMultiChoiceActivity.INPUT_CHECKED_LIST_ITEMS_IDS);
            if (checkedIds != null) {
                inputMarks = new ArrayList<>(checkedIds.length);
                for (long id : checkedIds)
                    inputMarks.add(id);
            }
        }
        db = ((IWorkWithDatabaseActivity) activity).getDatabase();

        mAdapter = new MarksCursorMultiAdapter(activity, null, this, inputMarks);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null){
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null){
            Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
            actionMode = toolbar.startActionMode(actionModeCallback);
        }
        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(getString(R.string.overlay_text) + " " + count));
            actionMode.invalidate();
        }
    }


    @Override
    public void notifyList() {
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
        return new CursorLoader(activity, null, null, null, null, null){
            @Override
            public Cursor loadInBackground() {
                return db.selectMarksAndPhotosCountCursor(userId);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_marks_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.item_check_confirm:
                    //Получаю массив выбранных Id элементов списка.
                    long[] markIds = getSelectedItemsIdsArray();
                    //Возвращаю массив обратно в вызвавшую Активити.
                    Intent backIntent = new Intent();
                    backIntent.putExtra(MarksMultiChoiceActivity.OUTPUT_CHECKED_LIST_ITEMS_IDS, markIds);
                    activity.setResult(Activity.RESULT_OK, backIntent);
                    activity.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
        }
    }
}
