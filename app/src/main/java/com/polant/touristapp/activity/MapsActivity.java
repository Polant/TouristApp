package com.polant.touristapp.activity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepenz.materialdrawer.Drawer;
import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.base.BaseTouristActivity;
import com.polant.touristapp.drawer.NavigationDrawer;
import com.polant.touristapp.maps.clustering.CustomImageRenderer;
import com.polant.touristapp.maps.location.TouristLocationManager;
import com.polant.touristapp.model.clustering.MapClusterItem;
import com.polant.touristapp.model.database.UserMedia;

import java.io.File;
import java.util.ArrayList;

public class MapsActivity extends BaseTouristActivity implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_maps;

    private ClusterManager<MapClusterItem> mClusterManager;

    private TouristLocationManager mLocationManager;

    private Drawer mNavigationDrawer;

    private String mLastImagePath;

    private long[] mFilterMarks; //Фильтр фотографий, который хранит Id меток.

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initLocationManager();
        initMapFragment();
        initNavigationDrawer(initToolbar());
        initFAB();
    }

    //---------------------------------Геолокация и карта------------------------------------//

    private void initLocationManager() {
        mLocationManager = new TouristLocationManager(this);
    }

    private void initMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Обязательно, т.к. обработка геолокации на карте находится в TouristLocationManager!
        mLocationManager.setGoogleMap(googleMap);

        Location currentLocation = mLocationManager.getLastLocation();
        if (currentLocation != null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    Constants.DEFAULT_CAMERA_ZOOM_LEVEL));

            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title(getString(R.string.i_am_here));

            //Обязательно установить маркер, чтобы обновлять маркер своего местоположения.
            Marker myPosition = googleMap.addMarker(options);
            mLocationManager.setMyLocation(myPosition);
        }
        setUpClusterManager(googleMap);
    }

    //---------------------------Кластеризация------------------------------//

    private void setUpClusterManager(GoogleMap googleMap) {
        mClusterManager = new ClusterManager<>(this, googleMap);
        //Устанавливаю свой рендерер. Listener-ы и адаптеры устанавливаются внутри CustomImageRenderer.
        mClusterManager.setRenderer(new CustomImageRenderer(this, googleMap, mClusterManager));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<UserMedia> medias = db.selectAllUserMediaByUserId(mUserId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        addItemsToMap(medias);
                    }
                });
            }
        });
        t.start();
    }

    private void addItemsToMap(ArrayList<UserMedia> medias) {
        for (UserMedia media : medias){
            mClusterManager.addItem(new MapClusterItem(media));
        }
    }

    //Передать null, если надо вывести все фото.
    private void updateClustersByFilter(final long[] markIds){
        mClusterManager.clearItems();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<UserMedia> medias;
                if (markIds != null && markIds.length > 0){
                    medias = db.selectUserMediaByFilter(mUserId, markIds);
                }
                else{
                    medias = db.selectAllUserMediaByUserId(mUserId);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        addItemsToMap(medias);
                        mClusterManager.cluster();
                    }
                });
            }
        });
        t.start();
    }

    //---------------------Toolbar, Navigation Drawer and FAB----------------------------//

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        toolbar.inflateMenu(R.menu.toolbar_maps_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_filter:
                        //Фильтрация меток на карте.
                        Intent intent = new Intent(MapsActivity.this, MarksActivity.class);
                        intent.putExtra(Constants.USER_ID, mUserId);
                        intent.putExtra(MarksActivity.INPUT_CHECKED_LIST_ITEMS_IDS, mFilterMarks);
                        intent.putExtra(MarksActivity.CALL_FILTER_OR_ADD_MARKS, true);
                        startActivityForResult(intent, Constants.SHOW_MARKS_MULTI_CHOICE_ACTIVITY);
                        return true;
                    case R.id.item_reset_filter:
                        //Сбрасываю фильтр.
                        mFilterMarks = null;
                        updateClustersByFilter(null);
                        showSnackbar(findViewById(R.id.fab), R.string.reset_filter_text);
                        return true;
                }
                return false;
            }
        });
        return toolbar;
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        NavigationDrawer drawer = new NavigationDrawer(this, toolbar);
        mNavigationDrawer = drawer.getMaterialDrawer();
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File image = new File(getExternalFilesDir(null), "temporary.jpg");
                Uri outputFileUri = Uri.fromFile(image);

                //Путь к последнему сделанному изображению сохраняю в поле класса.
                mLastImagePath = outputFileUri.getPath();
                //Намерение для вызова камеры.
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(intent, Constants.TAKE_PHOTO);
            }
        });
    }

    //--------------------------------------------------------------------------//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.TAKE_PHOTO && resultCode == RESULT_OK) {
            if (data == null) {
                //После создания фото перехожу к его просмотру и сохранению.
                startSelectedPhotoActivity();
            }
        }
        else if (requestCode == Constants.SHOW_SELECTED_PHOTO_ACTIVITY && resultCode == RESULT_OK){
            openDatabase();
            //Обновляю кластеры после добавления нового фото.
            updateClustersByFilter(mFilterMarks);
            showSnackbar(findViewById(R.id.fab), R.string.reset_filter_text);
        }
        else if (requestCode == Constants.SHOW_SELECTED_PHOTO_ACTIVITY_FROM_INFO_WINDOW && resultCode == RESULT_OK){
            openDatabase();
            //Обновляю кластеры после клика но InfoWindow на карте.
            updateClustersByFilter(mFilterMarks);
        }
        else if (requestCode == Constants.SHOW_MARKS_MULTI_CHOICE_ACTIVITY && resultCode == RESULT_OK){
            openDatabase();
            if (data != null){//Обновляю метки на карте.
                long[] marksIds = data.getLongArrayExtra(MarksActivity.OUTPUT_CHECKED_LIST_ITEMS_IDS);
                mFilterMarks = marksIds;
                updateClustersByFilter(marksIds);
            }
        }
        else if (requestCode == Constants.SHOW_MARKS_ACTIVITY){
            openDatabase();
            updateClustersByFilter(mFilterMarks);
        }else if (requestCode == Constants.SHOW_SETTINGS_ACTIVITY){
            //Сделал обновление данные для работы TouristLocationManager в его методе
            //registerListener(), который вызывается в onResume().
        }
    }

    private void startSelectedPhotoActivity(){
        Intent intent = new Intent(this, SelectedPhotoActivity.class);

        intent.putExtra(SelectedPhotoActivity.IMAGE_EXTERNAL_PATH, mLastImagePath);
        intent.putExtra(Constants.USER_ID, mUserId);
        intent.putExtra(SelectedPhotoActivity.IMAGE_LOCATION, mLocationManager.getLastLocation());

        startActivityForResult(intent, Constants.SHOW_SELECTED_PHOTO_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen()) {
            mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.unregisterListener();
    }
}
