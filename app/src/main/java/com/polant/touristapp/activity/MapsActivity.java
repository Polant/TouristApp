package com.polant.touristapp.activity;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepenz.materialdrawer.Drawer;
import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.drawer.NavigationDrawer;
import com.polant.touristapp.maps.clustering.CustomImageRenderer;
import com.polant.touristapp.maps.clustering.MapClusterItem;
import com.polant.touristapp.model.UserMedia;

import java.io.File;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_maps;
    private static final int TAKE_PHOTO = 0;//передается в startActivityForResult() для получения фото.
    private static final int SHOW_SELECTED_PHOTO_ACTIVITY = 1;
    private static final int SHOW_MARKS_ACTIVITY = 2;

    private GoogleMap mMap;
    private ClusterManager<MapClusterItem> mClusterManager;

    private Database db;    //База данных.

    private LocationManager locationManager;
    private Criteria criteria;
    private Location currentLocation;   //Текущее местоположение.

    private Drawer navigationDrawer;

    private String lastImagePath; //Путь к последнему созданному изображению.
    private final int userId = Constants.DEFAULT_USER_ID_VALUE; //Id пользователя по умолчанию.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        initLocationManager();
        initMapFragment();
        initNavigationDrawer(initToolbar());
        initFAB();
    }

    //База открывается и закрывается в onStart() и onStop().
    private void openDatabase(){
        db = new Database(this);
        db.open();  //А закрываю в OnStop().
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (currentLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    Constants.DEFAULT_CAMERA_ZOOM_LEVEL));
        }
        //Устанавливаю менеджер кластеризации.
        setUpClusterer();
    }

    private void initMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //---------------------------Кластеризация------------------------------//

    private void setUpClusterer() {
        //Создаю менеджер кластеризации.
        mClusterManager = new ClusterManager<>(this, mMap);
        //устанавливаю рендерер. Listener-ы и адаптеры устанавливаются внутри CustomImageRenderer.
        mClusterManager.setRenderer(new CustomImageRenderer(this, mMap, mClusterManager));

        addItemsToMap();
    }

    private void addItemsToMap() {
        ArrayList<UserMedia> medias = db.selectAllUserMediaByUserId(userId);
        for (UserMedia media : medias){
            mClusterManager.addItem(new MapClusterItem(media));
        }
    }

    //--------------------------------------------------------------//

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        toolbar.inflateMenu(R.menu.toolbar_maps_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO: реализовать обработчик фильтрации.
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_filter:
                        //TODO: этот код отсюда нужно убрать.
                        Intent intent = new Intent(MapsActivity.this, MarksActivity.class);
                        intent.putExtra(Constants.USER_ID, userId);
                        intent.putExtra(MarksActivity.IS_ADD_MARK_TO_PHOTO, true);

                        startActivityForResult(intent, SHOW_MARKS_ACTIVITY);
                        return true;
                }
                return false;
            }
        });
        return toolbar;
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        NavigationDrawer drawer = new NavigationDrawer(this, toolbar);
        navigationDrawer = drawer.getMaterialDrawer();
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File image = new File(getExternalFilesDir(null), "temporary.jpg");
                Uri outputFileUri = Uri.fromFile(image);

                //Путь к последнему сделанному изображению сохраняю в поле класса.
                lastImagePath = outputFileUri.getPath();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            if (data == null) {
                //После создания фото перехожу к его просмотру и сохранению.
                startSelectedPhotoActivity();
            }
        }
        else if (requestCode == SHOW_SELECTED_PHOTO_ACTIVITY && resultCode == RESULT_OK){
            openDatabase();
            //Обновляю кластеры после добавления нового фото.
            updateClusters();
        }
        else if (requestCode == SHOW_MARKS_ACTIVITY && resultCode == RESULT_OK){
        }
    }

    private void startSelectedPhotoActivity(){
        Intent intent = new Intent(this, SelectedPhotoActivity.class);

        //Передаю путь к изображению, Id пользователя, текущее местоположение.
        intent.putExtra(SelectedPhotoActivity.IMAGE_EXTERNAL_PATH, lastImagePath);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(SelectedPhotoActivity.IMAGE_LOCATION, currentLocation);

        startActivityForResult(intent, SHOW_SELECTED_PHOTO_ACTIVITY);
    }

    private void updateClusters() {
        mClusterManager.clearItems();
        addItemsToMap();
    }


    @Override
    public void onBackPressed() {
        if (navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    //------------------------------Открытие и закрытие базы----------------------------//


    @Override
    protected void onStart() {
        super.onStart();
        openDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }

    //------------Регистрация и отмена регистрации слушателя геолокации------------------//

    @Override
    protected void onResume() {
        super.onResume();
        registerLocationListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterLocationListener();
    }

    //---------------------------------Геолокация--------------------------------------//

    private void initLocationManager() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Составляю критерии выбора лучшего провайдера.
        criteria = initCriteria();
        //Выбираю лучщий провайдер.
        String provider = locationManager.getBestProvider(criteria, true);
        //Сразу делаю запрос на местоположение.
        //locationManager.requestSingleUpdate(provider, locationListener, null);
        Location l = locationManager.getLastKnownLocation(provider);
        updateMapWithLocation(l);
    }

    private Criteria initCriteria(){
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setPowerRequirement(Criteria.POWER_LOW);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);

        return c;
    }

    //Обновление местоположения.
    private void updateMapWithLocation(Location l) {
        //TODO: сделать обработку изменения локации.
        currentLocation = l;
//        if (mMap != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(l.getLatitude(), l.getLongitude())));
//        }
    }

    private void registerLocationListener(){
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider,
                Constants.LOCATION_UPDATE_FREQUENCY,
                Constants.LOCATION_UPDATE_MIN_DISTANCE,
                locationListener);
    }

    private void unregisterLocationListener(){
        locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateMapWithLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };
}
