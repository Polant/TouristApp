package com.polant.touristapp.maps.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.polant.touristapp.Constants;

/**
 * Класс-обертка для работы с результатами геолокации на карте.
 */
public class TouristLocationManager implements ILocationManager{

    private Context mContext;
    private LocationManager mLocationManager;
    private Criteria mCriteria;
    private Location mLocation;

    private GoogleMap mMap;

    public TouristLocationManager(Context mContext) {
        this.mContext = mContext;

        initCriteria();
        initLocationManager();
    }

    public TouristLocationManager setGoogleMap(GoogleMap mMap) {
        this.mMap = mMap;
        return this;
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
        if (mMap != null){
            //TODO: сделать обработку изменения локации.
        }
    }

    @Override
    public void registerListener() {
        String provider = mLocationManager.getBestProvider(mCriteria, true);
        mLocationManager.requestLocationUpdates(provider,
                Constants.LOCATION_UPDATE_FREQUENCY,
                Constants.LOCATION_UPDATE_MIN_DISTANCE,
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
