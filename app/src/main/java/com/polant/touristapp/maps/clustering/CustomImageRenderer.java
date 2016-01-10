package com.polant.touristapp.maps.clustering;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.polant.touristapp.ImageUtils;
import com.polant.touristapp.R;
import com.polant.touristapp.maps.drawable.MultiDrawable;
import com.polant.touristapp.model.UserMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Антон on 10.01.2016.
 */
public class CustomImageRenderer extends DefaultClusterRenderer<MapClusterItem> {

    private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;
    private final ImageView mImageView;
    private final ImageView mClusterImageView;
    private final int mDimension;

    private final Activity mContext;

    public CustomImageRenderer(Activity context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
        mIconGenerator = new IconGenerator(context);
        mClusterIconGenerator = new IconGenerator(context);

        View multiImage = context.getLayoutInflater().inflate(R.layout.cluster_image_layout, null);
        mClusterIconGenerator.setContentView(multiImage);
        mClusterImageView = (ImageView)multiImage.findViewById(R.id.image);

        mImageView = new ImageView(context);
        mDimension = (int) context.getResources().getDimension(R.dimen.custom_cluster_image);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));

        int padding = (int)context.getResources().getDimension(R.dimen.custom_cluster_text_padding);
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        UserMedia userMedia = item.getMedia();

        //Тут нужно каким то образом установить InfoWindow.

        mImageView.setImageURI(Uri.parse(userMedia.getMediaExternalPath()));
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
                .title(userMedia.getName())
                .snippet(userMedia.getDescription());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MapClusterItem> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);

        //Рисуем мульти-иконку.
        List<Drawable> photos = new ArrayList<>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        for (MapClusterItem item : cluster.getItems()){
            if (photos.size() == 4) break;

            Drawable drawable = new BitmapDrawable(mContext.getResources(),
                    ImageUtils.createBitmap(item.getMedia().getMediaExternalPath(), width, height));

            drawable.setBounds(0, 0, width, height);
            photos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(photos);
        multiDrawable.setBounds(0, 0, width, height);

        mClusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MapClusterItem> cluster) {
        return cluster.getSize() > 1;
    }
}
