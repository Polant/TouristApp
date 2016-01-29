package com.polant.touristapp.adapter.recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.polant.touristapp.R;
import com.polant.touristapp.adapter.base.RecyclerClickListener;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.model.Mark;
import com.polant.touristapp.model.UserMedia;
import com.polant.touristapp.model.recycler.RecyclerItem;
import com.polant.touristapp.utils.image.ImageUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Антон on 29.01.2016.
 */
public class SearchMultiTypesAdapter extends RecyclerView.Adapter<SearchMultiTypesAdapter.SearchHolder>{

    private static final int TYPE_MARK = 0;
    private static final int TYPE_PHOTO = 1;

    private static final int LAYOUT_MARK = R.layout.multi_recycler_item_mark;
    private static final int LAYOUT_PHOTO = R.layout.multi_recycler_item_photo;

    private Context mContext;
    private List<RecyclerItem> mItems;
    private RecyclerClickListener mClickListener;
    private LayoutInflater mInflater;

    private static Handler mHandler = new Handler();

    public SearchMultiTypesAdapter(Context mContext, List<RecyclerItem> mItems, RecyclerClickListener mClickListener) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.mClickListener = mClickListener;
        this.mInflater = LayoutInflater.from(mContext);

        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isMark() ? TYPE_MARK : TYPE_PHOTO;
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int layout = viewType == TYPE_MARK ? LAYOUT_MARK : LAYOUT_PHOTO;

        View view = mInflater.inflate(layout, parent, false);
        return new SearchHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {
        holder.bindData(mContext, mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final RecyclerClickListener mListener;

        //Метки.
        private CircularImageView markImageView;
        private TextView textMarkName;
        private final TextView textMarkDescription;
        //Фото.
        private CircularImageView photoImageView;
        private TextView textPhotoName;
        private TextView textPhotoDescription;
        private TextView textPhotoCreatedDate;

        public SearchHolder(View itemView, RecyclerClickListener mClickListener) {
            super(itemView);

            mListener = mClickListener;

            markImageView = (CircularImageView) itemView.findViewById(R.id.imageMultiRecyclerMark);
            textMarkName = (TextView) itemView.findViewById(R.id.textMultiRecyclerMarkName);
            textMarkDescription = (TextView) itemView.findViewById(R.id.textMultiRecyclerMarkDescription);

            photoImageView = (CircularImageView) itemView.findViewById(R.id.imageMultiRecyclerPhoto);
            textPhotoName = (TextView) itemView.findViewById(R.id.textMultiRecyclerPhotoName);
            textPhotoDescription = (TextView) itemView.findViewById(R.id.textMultiRecyclerPhotoDescription);
            textPhotoCreatedDate = (TextView) itemView.findViewById(R.id.textMultiRecyclerPhotoCreatedDate);

            itemView.setOnClickListener(this);
        }

        public void bindData(final Context context, RecyclerItem recyclerItem) {
            if (recyclerItem.isMark()){
                Mark mark = recyclerItem.getMark();

                markImageView.setImageResource(R.drawable.mark);
                textMarkName.setText(mark.getName());
                textMarkDescription.setText(mark.getDescription());
            }
            else if (recyclerItem.isUserMedia()){
                final UserMedia media = recyclerItem.getMedia();

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", getDateFormatSymbols(context));

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap photo = ImageUtils.createBitmap(media.getMediaExternalPath(),
                                (int) context.getResources().getDimension(R.dimen.multi_types_recycler_photo_width),
                                (int) context.getResources().getDimension(R.dimen.multi_types_recycler_photo_height));
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                photoImageView.setImageBitmap(photo);
                            }
                        });
                    }
                });
                t.start();

                textPhotoName.setText(media.getName());
                textPhotoDescription.setText(media.getDescription());
                textPhotoCreatedDate.setText(
                        String.format("%s %s", context.getString(R.string.photo_taken),
                                sdf.format(new Date(media.getCreatedDate()))));
            }
        }

        private static DateFormatSymbols getDateFormatSymbols(final Context context) {
            return new DateFormatSymbols(){
                @Override
                public String[] getMonths() {
                    return context.getResources().getStringArray(R.array.date_format_symbols);
                }
            };
        }

        @Override
        public void onClick(View v) {
            if (mListener != null){
                mListener.onItemClicked(getLayoutPosition());
            }
        }
    }
}
