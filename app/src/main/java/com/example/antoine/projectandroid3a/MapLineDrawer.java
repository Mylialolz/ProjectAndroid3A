package com.example.antoine.projectandroid3a;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Antoine on 25/09/2016.
 */

public class MapLineDrawer {


    public MapLineDrawer(){}


    public static PolylineOptions drawLineBetweenGeoPoints(double[][] geoPoints, float width, int color){

        PolylineOptions line = new PolylineOptions();

        for(int i = 0; i < geoPoints.length - 1; ++i){

                line.add(new LatLng(geoPoints[i][1], geoPoints[i][0])
                            , new LatLng(geoPoints[i+1][1], geoPoints[i+1][0])).width(width).color(color);

        }

        return line;
    }


}
