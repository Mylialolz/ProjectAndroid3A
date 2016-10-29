package com.example.antoine.projectandroid3a;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Antoine on 29/10/2016.
 */

public class CustomMarker implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSubTitle;

    public CustomMarker(double lat, double lng, String title, String subTitle) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSubTitle = subTitle;
    }

    public CustomMarker(LatLng pos, String title, String subTitle) {
        mPosition = pos;
        mTitle = title;
        mSubTitle = subTitle;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }


    public String getTitle(){
        return mTitle;
    }

    public String getSubTitle(){
        return mSubTitle;
    }

}