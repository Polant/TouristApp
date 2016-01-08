package com.polant.touristapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.polant.touristapp.R;
import com.polant.touristapp.drawer.NavigationDrawer;

import java.io.File;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_maps;
    private static final int TAKE_PHOTO = 0;//передается в startActivityForResult() для получения фото;
    private static final int SHOW_SELECTED_PHOTO_ACTIVITY = 1;

    private GoogleMap mMap;

    private Drawer navigationDrawer;

    private String lastImagePath;           //Путь к последнему созданному изображению.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        initMapFragment();
        initNavigationDrawer(initToolbar());
        initFAB();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void initMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        toolbar.inflateMenu(R.menu.toolbar_maps_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO: реализовать обработчик фильтрации.
                int id = item.getItemId();
                switch (id){
                    case R.id.item_filter:
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

    private void initFAB(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: сделать генерацию имени файла.
                File image = new File(getExternalFilesDir(null), "test.jpg");
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

        if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK){
            if (data == null){
                Intent intent = new Intent(this, SelectedPhotoActivity.class);
                intent.putExtra(SelectedPhotoActivity.IMAGE_EXTERNAL_PATH, lastImagePath);

                startActivityForResult(intent, SHOW_SELECTED_PHOTO_ACTIVITY);
            }
        }
    }
}
