package com.polant.touristapp.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.adapter.recycler.PhotosCursorMultiAdapter;
import com.polant.touristapp.fragment.base.BaseRecyclerFragment;

/**
 * Created by Антон on 25.01.2016.
 */
public class PhotosFragment extends BaseRecyclerFragment {

    private static final int LAYOUT = R.layout.fragment_photos_recycler;

    public static final String INPUT_MARK_ID = "INPUT_MARK_ID";

    private View view;

    private long mMarkId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new PhotosCursorMultiAdapter(mActivity, null, this, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPhotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mAdapter);

        mActionModeCallback = new ActionModeCallback();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void getDataFromArguments(Bundle args) {
        super.getDataFromArguments(args);
        if (args != null){
            mMarkId = args.getLong(INPUT_MARK_ID, -1);
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
        }else {
            //TODO: сделать переход на SelectedPhotoActivity.
            Log.d(Constants.APP_LOG_TAG,
                    String.format("Photo №%d, Id:%d", position, mAdapter.getItemId(position)));
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

    //----------------Обработка пунктов меню ActionMode-------------------//

    private void removePhotos() {
        //TODO: обработать удаление фото.
        Log.d(Constants.APP_LOG_TAG, "Удаление фото");
    }

    //------------------------LoaderCallback--------------------------//

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity, null, null, null, null, null){
            @Override
            public Cursor loadInBackground() {
                return db.selectCursorUserMediaByFilter(mUserId, new long[]{mMarkId});
            }
        };
    }

    //---------------------------------------------------------------------//

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_photos_remove, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case R.id.item_remove_photo:
                    removePhotos();
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
