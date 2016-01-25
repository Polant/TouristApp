package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksActivity;
import com.polant.touristapp.adapter.recycler.MarksCursorMultiAdapter;
import com.polant.touristapp.fragment.base.BaseRecyclerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Антон on 25.01.2016.
 */
public class MarksFragment extends BaseRecyclerFragment {

    private static final int LAYOUT = R.layout.fragment_marks_recycler_multi_choice;

    private View view;

    private List<Long> inputMarks;

    //true - если Активити вызвана для фильтрования фото по меткам на карте
    //или добавления меток для нового фото.
    private boolean isCallToFilterOrAddMarksToPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new MarksCursorMultiAdapter(mActivity, null, this, inputMarks);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mAdapter);

        mActionModeCallback = new ActionModeCallback();
        getLoaderManager().initLoader(0, null, this);

        initActionMode();
    }

    @Override
    protected void getDataFromArguments(Bundle args) {
        super.getDataFromArguments(args);//Там получаю USER_ID.
        if (args != null) {
            long[] checkedIds = args.getLongArray(MarksActivity.INPUT_CHECKED_LIST_ITEMS_IDS);
            if (checkedIds != null) {
                inputMarks = new ArrayList<>(checkedIds.length);
                for (long id : checkedIds)
                    inputMarks.add(id);
            }
            isCallToFilterOrAddMarksToPhoto = args.getBoolean(MarksActivity.CALL_FILTER_OR_ADD_MARKS);
        }
    }

    private void initActionMode() {
        //Если переданы начальные выделенные элементы для RecyclerView.
        if (mActionMode == null && inputMarks != null){
            startActionMode();
            refreshActionMode(inputMarks.size());
        }
    }

    @Override
    protected void startActionMode() {
        Toolbar toolbar = (Toolbar)mActivity.findViewById(R.id.toolbar);
        mActionMode = toolbar.startActionMode(mActionModeCallback);
    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        }
        else if (!isCallToFilterOrAddMarksToPhoto){
            Log.d(Constants.APP_LOG_TAG, "Транзакция фрагментов");
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (mActionMode == null){
            startActionMode();
        }
        toggleSelection(position);
        return true;
    }

    //------------------Обработка пунктов меню ActionMode------------------//

    private void finishFiltration() {
        //Получаю массив выбранных Id элементов списка.
        long[] markIds = getSelectedItemsIdsArray();
        //Возвращаю массив обратно в вызвавшую Активити.
        Intent backIntent = new Intent();
        backIntent.putExtra(MarksActivity.OUTPUT_CHECKED_LIST_ITEMS_IDS, markIds);
        mActivity.setResult(Activity.RESULT_OK, backIntent);
        mActivity.finish();
    }

    private void removeMarks() {
        //TODO: сделать удаление.
    }

    //---------------------------------------------------------------------//

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            if (isCallToFilterOrAddMarksToPhoto){
                inflater.inflate(R.menu.toolbar_marks_selected, menu);
            }
            else{
                inflater.inflate(R.menu.toolbar_marks_remove, menu);
            }
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
                    finishFiltration();
                    return true;
                case R.id.item_remove_mark:
                    removeMarks();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            mActionMode = null;
        }
    }
}
