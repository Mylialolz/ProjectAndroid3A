package com.example.antoine.projectandroid3a;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomMapFragment extends Fragment {

    private DataFromHttpRequest tunnel;
    private List<PisteReseauCyclable> mDataList;

    private int mItemToFocusOn;
    private LatLng markerToFocusOn;

    private MapView mMapView;
    private GoogleMap googleMap;


    public CustomMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_map, container, false);
        // Inflate the layout for this fragment

        mItemToFocusOn = Integer.valueOf(this.getArguments().getString(MainActivity.ITEM_TO_FOCUS_ON));
        mDataList = tunnel.getDataList();

        double[] tempInit = mDataList.get(mItemToFocusOn).getGeo_point_2d();
        markerToFocusOn = new LatLng(tempInit[0], tempInit[1]);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map

                for(int i = 0; i < mDataList.size(); ++i){

                    PisteReseauCyclable data = mDataList.get(i);
                    LatLng latCourante = new LatLng(data.getGeo_point_2d()[0], data.getGeo_point_2d()[1]);
                    googleMap.addMarker(new MarkerOptions().position(latCourante).title(data.getCompleteStreetNameWithArdt()).snippet(data.getSens_velo()));

                    PolylineOptions line = MapLineDrawer.drawLineBetweenGeoPoints(data.getGeo_shape().getCoordinates()
                                                                                    , 4
                                                                                    , Color.RED);

                    Polyline _line = googleMap.addPolyline(line);
                }

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(markerToFocusOn).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        tunnel = (DataFromHttpRequest) context;
    }

}
