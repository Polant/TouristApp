package com.polant.touristapp.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Антон on 07.01.2016.
 */
public class MapClusterItem implements ClusterItem {

    private final LatLng position;

    public MapClusterItem(double x, double y) {
        this.position = new LatLng(x, y);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}
