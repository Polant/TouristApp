package com.polant.touristapp.adapter.recycler;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.polant.touristapp.R;
import com.polant.touristapp.adapter.base.CursorRecyclerViewMultiAdapter;
import com.polant.touristapp.adapter.base.RecyclerClickListener;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.utils.date.DateFormatUtil;
import com.polant.touristapp.utils.image.ImageUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Адаптер для RecyclerView, содержащего фото.
 */
public class PhotosCursorMultiAdapter extends CursorRecyclerViewMultiAdapter<PhotosCursorMultiAdapter.PhotoViewHolder>{

    private static final int TYPE_SELECTED = 0;
    private static final int TYPE_NON_SELECTED = 1;

    private static final int LAYOUT_SELECTED = R.layout.recycler_item_photo_selected;
    private static final int LAYOUT_NON_SELECTED = R.layout.recycler_item_photo;

    private Context mContext;
    private LayoutInflater mInflater;
    private RecyclerClickListener mClickListener;

    private static Handler mHandler = new Handler();


    //Начальное выделение.
    private List<Long> mInputData;

    public PhotosCursorMultiAdapter(Context context, @Nullable Cursor cursor,
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
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int layout = viewType == TYPE_SELECTED ? LAYOUT_SELECTED: LAYOUT_NON_SELECTED;

        View itemView = mInflater.inflate(layout, parent, false);
        return new PhotoViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, Cursor c) {
        int pos = c.getPosition();
        /*Не знаю почему, но курсор будет иметь не ту позицию, если просто его передать
        * в bindData(), несмотря на то, что в данный момент от имеет верную позицию. Передача
        * текущей позиции рещает проблему. Не могу понять почему так?!*/
        holder.bindData(mContext, c, pos);
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private RecyclerClickListener mListener;

        private ImageView imageView;
        private TextView textName;
        private TextView textDescription;
        private TextView textCreatedDate;

        public PhotoViewHolder(View itemView, RecyclerClickListener mClickListener) {
            super(itemView);
            mListener = mClickListener;

            imageView = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
            textName = (TextView) itemView.findViewById(R.id.textPhotoName);
            textDescription = (TextView) itemView.findViewById(R.id.textPhotoDescription);
            textCreatedDate = (TextView) itemView.findViewById(R.id.textPhotoCreatedDate);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindData(final Context context, Cursor c, int pos){
            if (c == null){
                return;
            }
            c.moveToPosition(pos);

            final String photoPath = c.getString(c.getColumnIndex(Database.MEDIA_EXTERNAL_PATH));
            String photoName = c.getString(c.getColumnIndex(Database.MEDIA_NAME));
            String photoDesc = c.getString(c.getColumnIndex(Database.MEDIA_DESCRIPTION));
            long photoCreated = c.getLong(c.getColumnIndex(Database.MEDIA_CREATED_DATE));

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", DateFormatUtil.getDateFormatSymbols(context));

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Использую 1 и максимальную высоту, т.к. мах высота = 200, и если передать ширину 1,
                    //то изображение "подгонится" по высоту. (см. ImageUtils.createBitmap).
                    final Bitmap photo = ImageUtils.createBitmap(photoPath, 1, (int)context.getResources().getDimension(R.dimen.recycler_item_image_view_height));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(photo);
                        }
                    });
                }
            });
            t.start();

            textName.setText(photoName);
            textDescription.setText(photoDesc);
            textCreatedDate.setText(
                    String.format("%s %s", context.getString(R.string.photo_taken),sdf.format(new Date(photoCreated))));
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
