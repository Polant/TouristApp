package com.polant.touristapp.model.clustering;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.polant.touristapp.model.database.UserMedia;

/**
 * Created by Антон on 07.01.2016.
 */
public class MapClusterItem implements ClusterItem {

    private final LatLng position;
    private UserMedia media;

    public MapClusterItem(UserMedia media) {
        this.media = media;
        position = new LatLng(media.getLatitude(), media.getLongitude());
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public UserMedia getMedia() {
        return media;
    }
}
