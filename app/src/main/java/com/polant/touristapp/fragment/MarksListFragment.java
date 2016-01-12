package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksActivity;
import com.polant.touristapp.adapter.MultiChoiceListAdapter;
import com.polant.touristapp.data.Database;

/**
 * A placeholder fragment containing a simple view.
 */
public class MarksListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private View view;
    private Activity activity;

    private MultiChoiceListAdapter multiAdapter;

    private Database db;

    private int userId;
    private boolean isAddMarkToPhoto;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IWorkWithDatabaseActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT IWorkWithDatabaseActivity");
        }
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marks_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null){
            Bundle args = getArguments();
            userId = args.getInt(Constants.USER_ID);
            isAddMarkToPhoto = args.getBoolean(MarksActivity.IS_ADD_MARK_TO_PHOTO);
        }
        //Получил уже открытую базу.
        db = ((IWorkWithDatabaseActivity) activity).getDatabase();

        ListView listViewMarks = (ListView) view.findViewById(R.id.listViewMarks);
        //Если выбран флаг добавления меток к выбранному фото, то ставлю "мульти-выбор".
        listViewMarks.setChoiceMode(
                isAddMarkToPhoto ? ListView.CHOICE_MODE_MULTIPLE : ListView.CHOICE_MODE_SINGLE);

        if (isAddMarkToPhoto) {
            multiAdapter = new MultiChoiceListAdapter(activity, null, 0);
            listViewMarks.setAdapter(multiAdapter);

            getLoaderManager().initLoader(0, null, this);
        }
        Log.d(Constants.APP_LOG_TAG, String.valueOf(isAddMarkToPhoto));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(activity, null, null, null, null, null){
            @Override
            public Cursor loadInBackground() {
                return db.selectAllMarksCursorByUserId(userId);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        multiAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        multiAdapter.swapCursor(null);
    }

    //ТОЛЬКО ДЛЯ ОТЛАДКИ.
    public void showSelectedItemsId() {
        ListView listViewMarks = (ListView) view.findViewById(R.id.listViewMarks);
        long[] ids = listViewMarks.getCheckedItemIds();
        for (long id : ids) {
            Log.d(Constants.APP_LOG_TAG, String.valueOf(id));
        }
    }
}
