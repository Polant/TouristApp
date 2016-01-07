package com.polant.touristapp.activity;

import android.net.Uri;
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
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.util.UIUtils;
import com.polant.touristapp.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_maps;
    private GoogleMap mMap;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initMapFragment();
        initToolbar();
        initNavigationDrawer();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void initMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void initNavigationDrawer() {
        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile profile = new ProfileDrawerItem().withName("Mike Penz")
                .withEmail("mikepenz@gmail.com")
                .withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");
        final IProfile profile2 = new ProfileDrawerItem().withName("Bernat Borras")
                .withEmail("alorma@github.com")
                .withIcon(Uri.parse("https://avatars3.githubusercontent.com/u/887462?v=3&s=460"));

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withTranslucentStatusBar(false)
                .addProfiles(
                        profile,
                        profile2
                )
                .build();

        Drawer result = new DrawerBuilder()
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
                .withGenerateMiniDrawer(true)
                .buildView();

        //the MiniDrawer is managed by the Drawer and we just get it to hook it into the Crossfader
        MiniDrawer miniResult = result.getMiniDrawer();

        //get the widths in px for the first and second panel
        int firstWidth = (int) UIUtils.convertDpToPixel(300, this);
        int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

        //create and build our crossfader (see the MiniDrawer is also builded in here, as the build method returns the view to be used in the crossfader)
        //the crossfader library can be found here: https://github.com/mikepenz/Crossfader
        final Crossfader crossFader = new Crossfader()
                .withContent(findViewById(R.id.contentMain))//тут я сам поменял.
                .withFirst(result.getSlider(), firstWidth)
                .withSecond(miniResult.build(this), secondWidth)
                .build();

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                crossFader.crossFade();
            }

            @Override
            public boolean isCrossfaded() {
                return crossFader.isCrossFaded();
            }
        });

        //define a shadow (this is only for normal LTR layouts if you have a RTL app you need to define the other one
        crossFader.getCrossFadeSlidingPaneLayout().setShadowResourceLeft(R.drawable.material_drawer_shadow_left);
    }


}
