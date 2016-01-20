package com.polant.touristapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;

/**
 * Created by Антон on 11.01.2016.
 */
public class MultiChoiceListAdapter extends CursorAdapter{

    private static final int LAYOUT = R.layout.list_item_mark_multi_choice;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MultiChoiceListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(LAYOUT, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView markerNameTextView = (TextView) view.findViewById(R.id.textViewMarkName);
        markerNameTextView.setText(cursor.getString(cursor.getColumnIndex(Database.MARK_NAME)));
    }
}
