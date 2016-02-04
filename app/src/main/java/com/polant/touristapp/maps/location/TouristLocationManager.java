package com.polant.touristapp.maps.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.SettingsActivity;

/**
 * Класс-обертка для работы с результатами геолокации на карте.
 */
public class TouristLocationManager implements ILocationManager{

    private Context mContext;
    private LocationManager mLocationManager;
    private Criteria mCriteria;
    private Location mLocation;

    private GoogleMap mMap;

    private Marker myLocation;

    public TouristLocationManager(Context mContext) {
        this.mContext = mContext;

        initCriteria();
        initLocationManager();
    }

    public void setGoogleMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void setMyLocation(Marker myLocation) {
        this.myLocation = myLocation;
    }

    private void initCriteria(){
        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        mCriteria.setAltitudeRequired(false);
        mCriteria.setBearingRequired(false);
        mCriteria.setSpeedRequired(false);
        mCriteria.setCostAllowed(true);
    }

    private void initLocationManager() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //Выбираю лучщий провайдер.
        String provider = mLocationManager.getBestProvider(mCriteria, true);
        Location l = mLocationManager.getLastKnownLocation(provider);
        updateMapWithLocation(l);
    }

    private void updateMapWithLocation(Location l) {
        mLocation = l;
        Log.d(Constants.APP_LOG_TAG, " map = " + mMap);
        if (mMap != null){
            Log.d(Constants.APP_LOG_TAG, " my location = " + l);
            Log.d(Constants.APP_LOG_TAG, " my location marker = " + myLocation);
            if(myLocation != null) {
                myLocation.setPosition(new LatLng(l.getLatitude(), l.getLongitude()));
            }
            else{
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(l.getLatitude(), l.getLongitude()))
                        .title(mContext.getString(R.string.i_am_here));

                myLocation = mMap.addMarker(options);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation.getPosition()));
            }
            Log.d(Constants.APP_LOG_TAG, " my location marker = " + myLocation);
        }
    }

    @Override
    public void registerListener() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        int locationUpdateFrequency = Integer.valueOf(sp.getString(SettingsActivity.KEY_LOCATION_UPDATE_FREQUENCY,
                String.valueOf(Constants.DEFAULT_LOCATION_UPDATE_FREQUENCY)));
        int locationUpdateMinDistance = Integer.valueOf(sp.getString(SettingsActivity.KEY_LOCATION_UPDATE_MIN_DISTANCE,
                String.valueOf(Constants.DEFAULT_LOCATION_UPDATE_MIN_DISTANCE)));

        String provider = mLocationManager.getBestProvider(mCriteria, true);
        Log.d(Constants.APP_LOG_TAG, " provider = " + provider);
        mLocationManager.requestLocationUpdates(provider,
                locationUpdateFrequency,
                locationUpdateMinDistance,
                mLocationListener);
    }

    @Override
    public void unregisterListener() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public Location getLastLocation() {
        return mLocation;
    }

    private final LocationListener mLocationListener = new LocationListener() {
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
