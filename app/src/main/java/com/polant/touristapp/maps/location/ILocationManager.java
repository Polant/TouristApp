package com.polant.touristapp.maps.location;

import android.location.Location;

/**
 * Created by Антон on 19.01.2016.
 */
public interface ILocationManager {
    void registerListener();
    void unregisterListener();
    Location getLastLocation();
}
