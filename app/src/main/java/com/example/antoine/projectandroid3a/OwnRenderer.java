package com.example.antoine.projectandroid3a;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Antoine on 29/10/2016.
 *  http://stackoverflow.com/questions/25711002/add-title-to-marker-in-google-map-cluster
 *  https://developers.google.com/maps/documentation/android-api/utility/?utm_source=welovemapsdevelopers&utm_campaign=mdr-utility
 */


public class OwnRenderer extends DefaultClusterRenderer<CustomMarker> {

    public OwnRenderer(Context context, GoogleMap map,
                       ClusterManager<CustomMarker> clusterManager) {
        super(context, map, clusterManager);
    }


    protected void onBeforeClusterItemRendered(CustomMarker item, MarkerOptions markerOptions) {

        markerOptions.snippet(item.getSubTitle());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
