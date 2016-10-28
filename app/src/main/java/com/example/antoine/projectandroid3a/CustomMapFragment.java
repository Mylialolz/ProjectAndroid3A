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

    private DataFromHttpRequest mTunnel; // mTunnel de communication entre l'activité et le fragment

    private List<PisteReseauCyclable> mDataListe; // donnees a maper
    private List<String> mMarkerUniciteListe; // sert a verifier l'unicite des markers affiches par nom de voie. Objectif : réduction du nombre de markers affiches

    private int mTargetItem; // index de l'item sur lequel on doit centrer la map par defaut à l'initialisation
    private LatLng mTargetMarker; // marker de l'item cible

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private ProgressBar mProgressBar; // progress bar
    private TextView mMessageChargement;

    public CustomMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_map, container, false);

        mTargetItem = Integer.valueOf(this.getArguments().getString(MainActivity.ITEM_TO_FOCUS_ON)); // recuperation de l'index de l'item sur lequel on doit centrer la map par defaut à l'initialisation
        mDataListe = mTunnel.getDataList(); // recuperation des donnees a maper


        mMarkerUniciteListe = new ArrayList<>();
        double[] tempInit = mDataListe.get(mTargetItem).getGeo_point_2d(); // recuperation des coordonnes du marker sur lequel la map sera centree par defaut
        mTargetMarker = new LatLng(tempInit[0], tempInit[1]); // affectation des coordonnes a la variable memebre

        mMapView = (MapView) rootView.findViewById(R.id.f_CustomMapFragment_mapView);

        mMapView.onCreate(savedInstanceState); // creation de la map view

        // configuration de la progressbar pour indiquer le niveau chargement à l'utilisateur
        mMessageChargement = (TextView)rootView.findViewById(R.id.f_CustomMapFragment_messageMapChargement);
        mMessageChargement.setText("La carte est cours de chargement...");

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.f_CustomMapFragment_progressBarMap);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(false);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(mDataListe.size());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                // type de vue de la map (satellite ou habituelle)
                if(MainActivity.getMapType().equals("SATELLITE")){mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);}
                else{mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);}

                // For showing a move to my location button
                mGoogleMap.setMyLocationEnabled(true);


                for(PisteReseauCyclable dataCourante : mDataListe){

                    LatLng latCourante = new LatLng(dataCourante.getGeo_point_2d()[0]
                                                                , dataCourante.getGeo_point_2d()[1]); // recuperation coordonnees marker


                    if(!SearchInList.contains(mMarkerUniciteListe, dataCourante.getNom_voie())) // verification de l'uncite du marker sur la map
                    {
                        mGoogleMap.addMarker(new MarkerOptions().position(latCourante) // ajout du marker correspond à la piste cyclable i
                                .title(dataCourante.getCompleteStreetNameWithArdt())
                                .snippet(dataCourante.getTypologie_simple()));

                        mMarkerUniciteListe.add(dataCourante.getNom_voie()); // acquittement de l'unicite
                    }

                    PolylineOptions ligneCourante = MapLineDrawer.drawLineBetweenGeoPoints(dataCourante.getGeo_shape().getCoordinates() // configuration de la piste cyclable
                                                                                    , 4                                 // epaisseur de la piste cyclable
                                                                                    , Color.RED);                       // couleur de la piste cyclable

                    mGoogleMap.addPolyline(ligneCourante); // dessin de la piste cyclable sur la map

                    mProgressBar.incrementProgressBy(1); // ne fonctionne pas
                }

                mMessageChargement.setText("");
                mProgressBar.setVisibility(View.GONE); // disparition de la progress bar
                mMapView.onResume(); // affichage de la map

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(mTargetMarker).zoom(10).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
        mTunnel = (DataFromHttpRequest) context;
    }



}
