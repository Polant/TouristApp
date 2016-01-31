package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;

import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksActivity;
import com.polant.touristapp.adapter.recycler.MarksCursorMultiAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.base.cursor.BaseRecyclerActionModeFragment;
import com.polant.touristapp.interfaces.ICollapsedToolbarActivity;
import com.polant.touristapp.model.Mark;
import com.polant.touristapp.model.UserMedia;
import com.polant.touristapp.utils.alert.AlertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Антон on 25.01.2016.
 */
public class MarksFragment extends BaseRecyclerActionModeFragment {

    public interface MarksListener {
        void showPhotosByMark(Mark mark);
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
        if (!(context instanceof MarksListener) || !(context instanceof ICollapsedToolbarActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT MarksListener and " +
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

    @Override
    protected void toggleSelection(int position) {
        super.toggleSelection(position);
        if (mActionMode != null) {
            int count = mAdapter.getSelectedItemCount();

            Menu actionMenu = mActionMode.getMenu();
            MenuItem changeMarkItem = actionMenu.findItem(R.id.item_change_mark);
            if (count == 1) {
                changeMarkItem.setVisible(true);
            } else {
                changeMarkItem.setVisible(false);
            }
        }
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
            Mark clicked = getDatabase().findMarkById(markId);
            ((MarksListener) mActivity).showPhotosByMark(clicked);
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
                case R.id.item_change_mark:
                    changeMarkDialog(getDatabase().findMarkById(
                            (long)mAdapter.getSelectedItemsIds().get(0)));
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

        private void changeMark(Mark mark, View fab, View alertView) {
            EditText nameText = (EditText) alertView.findViewById(R.id.editTextNewMarkName);
            EditText descriptionText = (EditText) alertView.findViewById(R.id.editTextNewMarkDescription);

            //TODO: сделать проверки на входные параметры: пустая строка и т.д.
            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();

            mark.setName(name);
            mark.setDescription(description);

            getDatabase().updateMark(mark);

            notifyRecyclerView();

            ((FloatingActionButton)fab).show();
            Snackbar.make(fab, R.string.mark_was_changed, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_close_text, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
        }

        private void changeMarkDialog(final Mark mark) {
            final View alertView = mActivity.getLayoutInflater().inflate(R.layout.alert_new_mark, null);

            EditText nameText = (EditText) alertView.findViewById(R.id.editTextNewMarkName);
            EditText descriptionText = (EditText) alertView.findViewById(R.id.editTextNewMarkDescription);
            nameText.setText(mark.getName());
            descriptionText.setText(mark.getDescription());

            DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    changeMark(mark, mActivity.findViewById(R.id.fab), alertView);
                }
            };

            AlertUtil.showAlertDialog(mActivity, R.string.alertChangeMarkTilte, R.string.alertChangeMarkMessage,
                    R.drawable.ic_bookmark, alertView, true, positiveListener, null);
        }
    }
}
