package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksMultiChoiceActivity;
import com.polant.touristapp.adapter.MultiChoiceListAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IListFragment;
import com.polant.touristapp.interfaces.IMultiChoiceListFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;
import com.polant.touristapp.interfaces.InitFABListener;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MarksListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IMultiChoiceListFragment {

    private View view;
    private Activity activity;

    private MultiChoiceListAdapter multiAdapter;

    private Database db;

    private int userId;
    private ArrayList<Long> inputMarks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IWorkWithDatabaseActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT IWorkWithDatabaseActivity " +
                    "and InitFABListener");
        }
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marks_list, container, false);
        //Инициализирую привязку FAB к ListView.
        ((InitFABListener)activity).initFAB((ListView) view.findViewById(R.id.listViewMarks));

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
        //Получил уже открытую базу.
        db = ((IWorkWithDatabaseActivity) activity).getDatabase();

        ListView listViewMarks = (ListView) view.findViewById(R.id.listViewMarks);
        //Ставлю "мульти-выбор".
        listViewMarks.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        multiAdapter = new MultiChoiceListAdapter(activity, null, 0);
        listViewMarks.setAdapter(multiAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    //Устанавливаю уже заранее выбранные элементы списка.
    private void setCheckedIds(){
        if (inputMarks == null)
            return;
        ListView listViewMarks = (ListView) view.findViewById(R.id.listViewMarks);
        for (int pos = 0; pos < listViewMarks.getCount(); pos++){
            long id = listViewMarks.getItemIdAtPosition(pos);
            if (inputMarks.contains(id)){
                listViewMarks.setItemChecked(pos, true);
            }
        }
    }


    //Реализация IListFragment.
    @Override
    public void notifyList() {
        getLoaderManager().restartLoader(0, null, this);
    }

    //Получаю список Id выбранных элементов списка.
    @Override
    public long[] getSelectedItemsIdsArray() {
        ListView listViewMarks = (ListView) view.findViewById(R.id.listViewMarks);
        return listViewMarks.getCheckedItemIds();
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
        setCheckedIds();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        multiAdapter.swapCursor(null);
    }
}
