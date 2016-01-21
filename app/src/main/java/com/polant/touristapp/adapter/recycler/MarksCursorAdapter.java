package com.polant.touristapp.adapter.recycler;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;

/**
 * Created by Антон on 21.01.2016.
 */
public class MarksCursorAdapter extends CursorRecyclerViewAdapter<MarksCursorAdapter.MarkViewHolder> {

    private LayoutInflater mInflater;
//    private View.OnClickListener mClickListener;
//    private View.OnLongClickListener mLongClickListener;

    public MarksCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(MarkViewHolder holder, Cursor c) {
        holder.bindData(c);
    }

    @Override
    public MarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_item_mark, parent, false);
        return new MarkViewHolder(itemView);
    }



    public static class MarkViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView imageView;
        private TextView textName;
        private TextView textDescription;

        public MarkViewHolder(View itemView) {
            super(itemView);

            imageView = (CircularImageView) itemView.findViewById(R.id.imageViewMark);
            textName = (TextView) itemView.findViewById(R.id.textMarkName);
            textDescription = (TextView) itemView.findViewById(R.id.textMarkDescription);
        }

        public void bindData(Cursor c){
            imageView.setImageResource(R.drawable.mark);
            textName.setText(c.getString(c.getColumnIndex(Database.MARK_NAME)));
            textDescription.setText(c.getString(c.getColumnIndex(Database.MARK_DESCRIPTION)));
        }
    }

}
