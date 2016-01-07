package com.polant.touristapp.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.polant.touristapp.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_maps;
    private GoogleMap mMap;
    private Toolbar toolbar;
    private Drawer navigationDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        initMapFragment();
        initToolbar();
        initNavigationDrawer();
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

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
//        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }

    private void initNavigationDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withTranslucentStatusBar(false)
                .build();

        navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Карта").withIcon(R.drawable.ic_map).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Поиск").withIcon(R.drawable.ic_magnify).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Метки").withIcon(R.drawable.ic_subject_black_24dp).withIdentifier(3),
                        new PrimaryDrawerItem().withName("Настройки").withIcon(R.drawable.ic_setting_dark).withIdentifier(4),
                        new PrimaryDrawerItem().withDescription("A more complex sample").withName("Помощь").withIcon(R.drawable.ic_help_black_24dp).withIdentifier(5),
                        new PrimaryDrawerItem().withName("Обратная связь").withIcon(R.drawable.ic_plus).withIdentifier(6)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .build();
    }

    private void initFAB(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "My fab action", Snackbar.LENGTH_LONG).show();
            }
        });
    }


}
