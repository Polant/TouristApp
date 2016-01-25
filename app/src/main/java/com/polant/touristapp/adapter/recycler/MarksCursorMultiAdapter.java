package com.polant.touristapp.adapter.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.polant.touristapp.R;
import com.polant.touristapp.adapter.base.CursorRecyclerViewMultiAdapter;
import com.polant.touristapp.adapter.base.RecyclerClickListener;
import com.polant.touristapp.data.Database;

import java.util.List;

/**
 * Created by Антон on 23.01.2016.
 */
public class MarksCursorMultiAdapter extends CursorRecyclerViewMultiAdapter<MarksCursorMultiAdapter.MarkViewHolder> {

    private static final int TYPE_SELECTED = 0;
    private static final int TYPE_NON_SELECTED = 1;

    private static final int LAYOUT_SELECTED = R.layout.recycler_item_mark_selected;
    private static final int LAYOUT_NON_SELECTED = R.layout.recycler_item_mark;

    private Context mContext;
    private LayoutInflater mInflater;
    private RecyclerClickListener mClickListener;

    //Начальное выделение.
    private List<Long> mInputData;

    public MarksCursorMultiAdapter(Context context, @Nullable Cursor cursor,
                                   RecyclerClickListener clickListener, @Nullable List<Long> inputData) {
        super(context, cursor);
        mContext = context;
        mClickListener = clickListener;
        mInputData = inputData;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        long id = getItemId(position);
        if (mInputData != null && mInputData.contains(id)){
            mInputData.remove(id);
            addSelection(position);
        }
        return isSelectedPosition(position) ? TYPE_SELECTED : TYPE_NON_SELECTED;
    }

    @Override
    public MarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int layout = viewType == TYPE_SELECTED ? LAYOUT_SELECTED: LAYOUT_NON_SELECTED;

        View itemView = mInflater.inflate(layout, parent, false);
        return new MarkViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(MarkViewHolder holder, Cursor c) {
        long id = c.getLong(c.getColumnIndex("_id"));
        int pos = c.getPosition();
        /*Не знаю почему, но курсор будет иметь не ту позицию, если просто его передать
        * в bindData(), несмотря на то, что в данный момент от имеет верную позицию. Передача
        * текущей позиции рещает проблему. Не могу понять почему так?!*/
        holder.bindData(mContext, c, pos, isSelectedId(id));
    }

    public static class MarkViewHolder extends RecyclerView.ViewHolder
                                        implements View.OnClickListener, View.OnLongClickListener{

        private RecyclerClickListener mListener;

        private CircularImageView imageView;
        private TextView textName;
        private TextView textPhotosCount;
        private View selectedOverlay;

        public MarkViewHolder(View itemView, RecyclerClickListener mClickListener) {
            super(itemView);
            mListener = mClickListener;

            imageView = (CircularImageView) itemView.findViewById(R.id.imageViewMark);
            textName = (TextView) itemView.findViewById(R.id.textMarkName);
            textPhotosCount = (TextView) itemView.findViewById(R.id.textMarkPhotosCount);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindData(Context context, Cursor c, int pos, boolean isSelected){
            if (c == null){
                return;
            }
            c.moveToPosition(pos);
            imageView.setImageResource(R.drawable.mark);
            textName.setText(c.getString(c.getColumnIndex(Database.MARK_NAME)));
            textPhotosCount.setText(String.format("%d %s",
                    c.getInt(c.getColumnIndex(Database.COUNT_PHOTOS_BY_MARK)),
                    context.getString(R.string.photo_text)));
            selectedOverlay.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClicked(getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mListener != null && mListener.onItemLongClicked(getLayoutPosition());
        }
    }

}
