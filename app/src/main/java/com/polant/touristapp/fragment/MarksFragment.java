package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksActivity;
import com.polant.touristapp.adapter.recycler.MarksCursorMultiAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.base.BaseRecyclerFragment;
import com.polant.touristapp.interfaces.ICollapsedToolbarActivity;
import com.polant.touristapp.model.UserMedia;
import com.polant.touristapp.utils.alert.AlertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Антон on 25.01.2016.
 */
public class MarksFragment extends BaseRecyclerFragment {

    public interface MarksFragmentListener {
        void showPhotosByMark(long markId);
    }

    private static final int LAYOUT = R.layout.fragment_marks_recycler;

    private View view;

    private List<Long> mInputMarks;

    //true - если Активити вызвана для фильтрования фото по меткам на карте
    //или добавления меток для нового фото.
    private boolean isCallToFilterOrAddMarksToPhoto;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof MarksFragmentListener) || !(context instanceof ICollapsedToolbarActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT MarksFragmentListener and " +
                    "ICollapsedToolbarActivity");
        }
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

        mAdapter = new MarksCursorMultiAdapter(mActivity, null, this, mInputMarks);

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
                mInputMarks = new ArrayList<>(checkedIds.length);
                for (long id : checkedIds)
                    mInputMarks.add(id);
            }
            isCallToFilterOrAddMarksToPhoto = args.getBoolean(MarksActivity.CALL_FILTER_OR_ADD_MARKS);
        }
    }

    private void initActionMode() {
        //Если переданы начальные выделенные элементы для RecyclerView.
        if (mActionMode == null && mInputMarks != null){
            startActionMode();
            refreshActionMode(mInputMarks.size());
        }
    }

    @Override
    protected void startActionMode() {
        Toolbar toolbar = (Toolbar)mActivity.findViewById(R.id.toolbar);
        mActionMode = toolbar.startActionMode(mActionModeCallback);
    }

    //------------------------RecyclerClickListener--------------------------//

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        }
        else if (!isCallToFilterOrAddMarksToPhoto){
            //Выполняю замену данного фрагмента.
            long markId = mAdapter.getItemId(position);
            ((MarksFragmentListener)mActivity).showPhotosByMark(markId);
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

    //--------------------------LoaderCallback-----------------------------//

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity, null, null, null, null, null){
            @Override
            public Cursor loadInBackground() {
                return getDatabase().selectMarksAndPhotosCountCursor(mUserId);
            }
        };
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
            ((ICollapsedToolbarActivity)mActivity).changeCollapsedToolbarLayoutBackground(true);
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
                    removeMarksDialog();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            ((ICollapsedToolbarActivity)mActivity).changeCollapsedToolbarLayoutBackground(false);
            mAdapter.clearSelection();
            mActionMode = null;
        }

        private void finishFiltration() {
            //Получаю массив выбранных Id элементов списка.
            long[] markIds = getSelectedItemsIdsArray();
            //Возвращаю массив обратно в вызвавшую Активити.
            Intent backIntent = new Intent();
            backIntent.putExtra(MarksActivity.OUTPUT_CHECKED_LIST_ITEMS_IDS, markIds);
            mActivity.setResult(Activity.RESULT_OK, backIntent);
            mActivity.finish();
        }

        private void removeMarksWithTheirPhotos() {
            Database db = getDatabase();
            List<UserMedia> medias = db.selectUserMediaByFilter(mUserId, getSelectedItemsIdsArray());
            if (medias.size() > 0) {
                List<Long> mediasIds = new ArrayList<>(medias.size());
                for (UserMedia m : medias) {
                    mediasIds.add((long) m.getId());
                }
                db.deleteUserMedias(mediasIds);
            }
            db.deleteMarks(mAdapter.getSelectedItemsIds());

            mActionMode.finish();
            notifyRecyclerView();
        }

        private void removeMarksDialog() {
            DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeMarksWithTheirPhotos();
                }
            };

            AlertUtil.showAlertDialog(mActivity, R.string.alertDeleteMarksTitle, R.string.alertDeleteMarksConfirmMessage,
                    R.drawable.warning_orange, null, true, positiveListener, null);
        }
    }
}
