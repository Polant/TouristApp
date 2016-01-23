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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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
 * A placeholder fragment containing a simple view.
 */
public class MarksListMultiFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IMultiChoiceListFragment,
        MarksCursorMultiAdapter.MarkViewHolder.ClickListener {

    private static final int LAYOUT = R.layout.fragment_marks_list_multi_choice;

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

        mAdapter = new MarksCursorMultiAdapter(activity, null, this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(mAdapter);



        Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        ViewGroup toolbarParent = (ViewGroup) toolbar.getParent();
//        ViewGroup toolbarParent = ((ViewGroup) toolbar.getParent()).removeView(toolbar);
        actionModeCallback = new ActionModeCallback(toolbar, toolbarParent);

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
            actionMode = ((AppCompatActivity)activity).startSupportActionMode(actionModeCallback);
            actionMode.setCustomView(actionModeCallback.toolbar);
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
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

//    //Устанавливаю уже заранее выбранные элементы списка.
//    private void setCheckedIds(){
//        if (inputMarks == null)
//            return;
//        ListView listViewMarks = (ListView) view.findViewById(R.id.listViewMarks);
//        for (int pos = 0; pos < listViewMarks.getCount(); pos++){
//            long id = listViewMarks.getItemIdAtPosition(pos);
//            if (inputMarks.contains(id)){
//                listViewMarks.setItemChecked(pos, true);
//            }
//        }
//    }

    //Реализация IListFragment.
    @Override
    public void notifyList() {
        getLoaderManager().restartLoader(0, null, this);
    }

    //Получаю список Id выбранных элементов списка.
    @Override
    public long[] getSelectedItemsIdsArray() {
//        ListView listViewMarks = (ListView) view.findViewById(R.id.listViewMarks);
//        return listViewMarks.getCheckedItemIds();
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
        //setCheckedIds();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private class ActionModeCallback implements ActionMode.Callback {

        private final Toolbar toolbar;
        private final ViewGroup toolbarParent;

        public ActionModeCallback(Toolbar toolbar, ViewGroup toolbarParent) {

            this.toolbar = toolbar;
            this.toolbarParent = toolbarParent;
        }

        private void removeToolbar(){
            toolbarParent.removeView(toolbar);
        }

        private void addToolbar(){
            toolbarParent.addView(toolbar);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_marks_selected, menu);
            removeToolbar();
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
                    Log.d(Constants.APP_LOG_TAG, "CLICKED_ACTION");
                    List<Long> selectedIds = mAdapter.getSelectedItemsIds();
                    for (long l : selectedIds){
                        Log.d(Constants.APP_LOG_TAG, String.valueOf(l));
                    }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            ((ViewGroup)actionMode.getCustomView().getParent()).removeView(toolbar);
            actionMode = null;
            addToolbar();
        }
    }
}