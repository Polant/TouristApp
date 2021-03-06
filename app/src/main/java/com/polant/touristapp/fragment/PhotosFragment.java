package com.polant.touristapp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polant.touristapp.R;
import com.polant.touristapp.adapter.recycler.PhotosCursorMultiAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.base.recycler.cursor.BaseRecyclerActionModeFragment;
import com.polant.touristapp.interfaces.ICollapsedToolbarActionModeActivity;
import com.polant.touristapp.model.database.UserMedia;
import com.polant.touristapp.utils.alert.AlertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Антон on 25.01.2016.
 */
public class PhotosFragment extends BaseRecyclerActionModeFragment {

    public interface PhotosListener {
        void showSelectedPhoto(UserMedia photo);
    }

    private static final int LAYOUT = R.layout.fragment_photos_recycler;

    public static final String INPUT_MARK_ID = "INPUT_MARK_ID";

    private View view;

    private long mMarkId;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof PhotosListener)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT PhotosListener");
        }
    }

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
            UserMedia clicked = getDatabase().findUserMediaById(mAdapter.getItemId(position));
            ((PhotosListener)mActivity).showSelectedPhoto(clicked);
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

    //------------------------LoaderCallback--------------------------//

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity, null, null, null, null, null){
            @Override
            public Cursor loadInBackground() {
                return getDatabase().selectCursorUserMediaByFilter(mUserId, new long[]{ mMarkId });
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        if (data == null || data.getCount() == 0){
            //Подсказка пользователю о том, что нет фото, связанных с выбранной меткой.
            TextView photoNotFound = (TextView)view.findViewById(R.id.textViewNoPhotos);
            photoNotFound.setVisibility(View.VISIBLE);
        }
    }

    //---------------------------------------------------------------------//

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_photos_remove, menu);
            if (mActivity instanceof ICollapsedToolbarActionModeActivity) {
                ((ICollapsedToolbarActionModeActivity) mActivity).changeCollapsedToolbarLayoutBackground(true);
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
            switch (id){
                case R.id.item_remove_photo:
                    removePhotosDialog();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (mActivity instanceof ICollapsedToolbarActionModeActivity) {
                ((ICollapsedToolbarActionModeActivity) mActivity).changeCollapsedToolbarLayoutBackground(false);
            }
            mAdapter.clearSelection();
            mActionMode = null;
        }

        private void removePhotos() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Long> photosIds = mAdapter.getSelectedItemsIds();

                    Database db = getDatabase();

                    List<UserMedia> photos = new ArrayList<>();
                    for (long id : photosIds){
                        photos.add(db.findUserMediaById(id));
                    }
                    db.deleteUserMedias(photos);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mActionMode.finish();
                            notifyRecyclerView();
                        }
                    });
                }
            });
            t.start();
        }

        private void removePhotosDialog() {
            DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removePhotos();
                }
            };

            AlertUtil.showAlertDialog(mActivity, R.string.alertDeletePhotoTitle, R.string.alertDeleteConfirmMessage,
                    R.drawable.warning_orange, null, true, positiveListener, null);
        }
    }
}
