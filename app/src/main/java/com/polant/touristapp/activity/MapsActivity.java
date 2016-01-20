package com.polant.touristapp.activity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.polant.touristapp.maps.location.TouristLocationManager;
import com.polant.touristapp.model.UserMedia;

import java.io.File;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_maps;

    private GoogleMap mMap;
    private ClusterManager<MapClusterItem> mClusterManager;
    private TouristLocationManager mLocationManager; //Работа с геолокацией.

    private Database db;    //База данных.

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

    private void openDatabase(){
        db = new Database(this);
        db.open();//База открывается и закрывается в onStart() и onStop().
    }

    //---------------------------------Геолокация и карта------------------------------------//

    private void initLocationManager() {
        mLocationManager = new TouristLocationManager(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Обязательно, т.к. обработка геолокации на карте находится в TouristLocationManager!
        mLocationManager.setGoogleMap(googleMap);

        Location currentLocation = mLocationManager.getLastLocation();
        if (currentLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    Constants.DEFAULT_CAMERA_ZOOM_LEVEL));
        }
        //Устанавливаю менеджер кластеризации.
        setUpClusterManager();
    }

    private void initMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //---------------------------Кластеризация------------------------------//

    private void setUpClusterManager() {
        mClusterManager = new ClusterManager<>(this, mMap);
        //Устанавливаю свой рендерер. Listener-ы и адаптеры устанавливаются внутри CustomImageRenderer.
        mClusterManager.setRenderer(new CustomImageRenderer(this, mMap, mClusterManager));

        ArrayList<UserMedia> medias = db.selectAllUserMediaByUserId(userId);
        addItemsToMap(medias);
    }

    private void addItemsToMap(ArrayList<UserMedia> medias) {
        for (UserMedia media : medias){
            mClusterManager.addItem(new MapClusterItem(media));
        }
    }

    //Передать null, если надо вывести все фото.
    private void updateClustersByFilter(long[] markIds){
        mClusterManager.clearItems();

        ArrayList<UserMedia> medias;
        if (markIds != null && markIds.length > 0){
            medias = db.selectUserMediaByFilter(userId, markIds);
        }
        else{
            medias = db.selectAllUserMediaByUserId(userId);
        }
        addItemsToMap(medias);
        mClusterManager.cluster();
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
                        Intent intent = new Intent(MapsActivity.this, MarksMultiChoiceActivity.class);
                        intent.putExtra(Constants.USER_ID, userId);
                        startActivityForResult(intent, Constants.SHOW_MARKS_MULTI_CHOICE_ACTIVITY);
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
            updateClustersByFilter(null);
        }
        else if (requestCode == Constants.SHOW_MARKS_MULTI_CHOICE_ACTIVITY && resultCode == RESULT_OK){
            openDatabase();
            if (data != null && data.getExtras() != null){//Обновляю метки на карте.
                long[] marksIds = data.getLongArrayExtra(MarksMultiChoiceActivity.OUT_LIST_ITEMS_IDS);
                updateClustersByFilter(marksIds);
            }
        }
    }

    private void startSelectedPhotoActivity(){
        Intent intent = new Intent(this, SelectedPhotoActivity.class);

        //Передаю путь к изображению, Id пользователя, текущее местоположение.
        intent.putExtra(SelectedPhotoActivity.IMAGE_EXTERNAL_PATH, lastImagePath);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(SelectedPhotoActivity.IMAGE_LOCATION, mLocationManager.getLastLocation());

        startActivityForResult(intent, Constants.SHOW_SELECTED_PHOTO_ACTIVITY);
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
        mLocationManager.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.unregisterListener();
    }

}
