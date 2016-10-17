package com.example.antoine.projectandroid3a;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomMapFragment extends Fragment {

    private DataFromHttpRequest tunnel; // tunnel de communication entre l'activité et le fragment

    private List<PisteReseauCyclable> mDataList; // donnees a maper

    private List<String> mNomVoieAffichee;

    private int mItemToFocusOn; // index de l'item sur lequel on doit centrer la map par defaut à l'initialisation
    private LatLng markerToFocusOn;

    private MapView mMapView;
    private GoogleMap googleMap;

    private ProgressBar progressBar; // progress bar
    private TextView messageChargement;

    public CustomMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_map, container, false);

        mItemToFocusOn = Integer.valueOf(this.getArguments().getString(MainActivity.ITEM_TO_FOCUS_ON)); // recuperation de l'index de l'item sur lequel on doit centrer la map par defaut à l'initialisation
        mDataList = tunnel.getDataList(); // recuperation des donnees a maper


        mNomVoieAffichee = new ArrayList<>();
        double[] tempInit = mDataList.get(mItemToFocusOn).getGeo_point_2d(); // recuperation des coordonnes du marker sur lequel la map sera centree par defaut
        markerToFocusOn = new LatLng(tempInit[0], tempInit[1]); // affectation des coordonnes a la variable memebre

        mMapView = (MapView) rootView.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState); // creation de la map view

        // configuration de la progressbar pour indiquer le niveau chargement à l'utilisateur
        messageChargement = (TextView)rootView.findViewById(R.id.messageMapChargement);
        messageChargement.setText("La carte est cours de chargement...");

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarMapFragment);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);
        progressBar.setMax(mDataList.size());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // type de vue de la map (satellite ou habituelle)
                if(MainActivity.getMapType().equals("SATELLITE")){mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);}
                else{mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);}

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);


                for(int i = 0; i < mDataList.size(); ++i){

                    PisteReseauCyclable data = mDataList.get(i); // recuperation de la ieme piste cyclable
                    LatLng latCourante = new LatLng(data.getGeo_point_2d()[0], data.getGeo_point_2d()[1]); // recuperation coordonnes marker

                    if(!SearchInList.contains(mNomVoieAffichee, data.getNom_voie())){
                        googleMap.addMarker(new MarkerOptions().position(latCourante) // ajout du marker correspond à la piste cyclable i
                                .title(data.getCompleteStreetNameWithArdt())
                                .snippet(data.getTypologie_simple()));

                        mNomVoieAffichee.add(data.getNom_voie());
                    }

                    PolylineOptions line = MapLineDrawer.drawLineBetweenGeoPoints(data.getGeo_shape().getCoordinates() // configuration de la piste cyclable
                                                                                    , 4                                 // epaisseur de la piste cyclable
                                                                                    , Color.RED);                       // couleur de la piste cyclable

                    googleMap.addPolyline(line); // dessin de la piste cyclable sur la map

                    progressBar.incrementProgressBy(1); // ne fonctionne pas
                }

                messageChargement.setText("");
                progressBar.setVisibility(View.GONE); // disparition de la progress bar
                mMapView.onResume(); // affichage de la map

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(markerToFocusOn).zoom(10).build();
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
