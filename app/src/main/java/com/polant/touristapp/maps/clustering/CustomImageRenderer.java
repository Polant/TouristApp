package com.polant.touristapp.maps.clustering;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.SelectedPhotoActivity;
import com.polant.touristapp.maps.drawable.MultiDrawable;
import com.polant.touristapp.model.UserMedia;
import com.polant.touristapp.utils.image.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomImageRenderer который передается менеджеру кластеризации.
 * Также содержит реализацию InfoWindowAdapter:
 * http://stackoverflow.com/questions/21885225/showing-custom-infowindow-for-android-maps-utility-library-for-android
 */
public class CustomImageRenderer extends DefaultClusterRenderer<MapClusterItem> {

    private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;

    private final ImageView mImageView;
    private final ImageView mClusterImageView;

    private final int mDimension;

    private final Activity mContext;

    //Сохраняю последний кластеризированный маркер.
    private MapClusterItem mClickedClusterItem;
//    private Cluster<MapClusterItem> clickedCluster;


    /**
     * IconGenerator хранит екземпляр ImageView. Это используется для задания иконки для маркера.
     * При задании новой иконки я меняю путь к файлу изображения или меняю Drawable в объекте
     * ImageView. А из IconGenerator-а получаю готовую иконку, которую передаю в markerOptions.icon().
     */
    public CustomImageRenderer(Activity context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);

        //Инициализирую данные для реализации InfoWindowAdapter, в котором
        //метки сопоставлены с соответствующими им объектами MapClusterItem.
        initInfoWindowAdapterSettings(context, map, clusterManager);

        mContext = context;
        mIconGenerator = new IconGenerator(context);
        mClusterIconGenerator = new IconGenerator(context);

        View multiImage = context.getLayoutInflater().inflate(R.layout.cluster_image_layout, null);
        mClusterIconGenerator.setContentView(multiImage);
        mClusterImageView = (ImageView)multiImage.findViewById(R.id.image);

        mImageView = new ImageView(context);
        mDimension = (int) context.getResources().getDimension(R.dimen.custom_cluster_image_size);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));

        int padding = (int)context.getResources().getDimension(R.dimen.custom_cluster_text_padding);
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);
    }

    private void initInfoWindowAdapterSettings(final Activity activity, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        map.setOnCameraChangeListener(clusterManager);//Обязательно, иначе кластеры не распадаются при увеличении масштаба.
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());

        /*Для целого кластера не вывожу InfoWindow, но чтоб это сделать нужно
           раскомментировать следующую строку, реализовать clusterManager.setOnClusterClickListener(),
           а также еще один InfoWindowAdapter.
         */
//        clusterManager.getClusterMarkerCollection()
//                .setOnInfoWindowAdapter(new ClusterInfoWindowAdapter());
        clusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new ClusterItemInfoWindowAdapter(activity));

        map.setOnMarkerClickListener(clusterManager);
//        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapClusterItem>() {
//            @Override
//            public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
//                clickedCluster = cluster;
//                return false;
//            }
//        });
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapClusterItem>() {
            @Override
            public boolean onClusterItemClick(MapClusterItem mapClusterItem) {
                mClickedClusterItem = mapClusterItem;
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (mClickedClusterItem == null){
                    return;
                }
                UserMedia clicked = mClickedClusterItem.getMedia();
                marker.hideInfoWindow();

                Intent intent = new Intent(activity, SelectedPhotoActivity.class);

                intent.putExtra(Constants.USER_ID, clicked.getUserId());
                intent.putExtra(SelectedPhotoActivity.INPUT_MEDIA, clicked);

                activity.startActivityForResult(intent, Constants.SHOW_SELECTED_PHOTO_ACTIVITY_FROM_INFO_WINDOW);
            }
        });
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        mImageView.setImageBitmap(ImageUtils.createBitmap(item.getMedia().getMediaExternalPath(),
                mDimension, mDimension));
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    /**
     * Если в этом методе оставить только вызов метода суперкласса,
     * то будет отображаться стандартное представление кластера в виде круга с числом в центре.
     */
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

    //---------------------------------Реализация InfoWindowAdapter-ов---------------------------//


    /**Адаптер для клика по одному единственному маркеру.*/
    private class ClusterItemInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        private final View mContents;

        private final int mInfoWindowWidth;
        private final int mInfoWindowHeight;

        public ClusterItemInfoWindowAdapter(Activity activity) {
            mWindow = activity.getLayoutInflater().inflate(R.layout.cluster_item_info_window, null);
            mContents = activity.getLayoutInflater().inflate(R.layout.cluster_item_info_window, null);

            mInfoWindowWidth = (int) activity.getResources().getDimension(R.dimen.default_info_window_width);
            mInfoWindowHeight = (int) activity.getResources().getDimension(R.dimen.default_info_window_height);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (mClickedClusterItem != null){
                render(mClickedClusterItem, mWindow);
            }
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (mClickedClusterItem != null){
                render(mClickedClusterItem, mContents);
            }
            return mContents;
        }

        private void render(MapClusterItem clusterItem, View view){
            UserMedia media = clusterItem.getMedia();

            ImageView photo = (ImageView) view.findViewById(R.id.imageClusterItemInfoWindow);
            photo.setImageBitmap(ImageUtils.createBitmap(
                    media.getMediaExternalPath(), mInfoWindowWidth, mInfoWindowHeight));

            TextView name = (TextView) view.findViewById(R.id.textViewClusterItemName);
            name.setText(media.getName());

            TextView description = (TextView) view.findViewById(R.id.textViewClusterItemDescription);
            description.setText(media.getDescription());
        }
    }
}
