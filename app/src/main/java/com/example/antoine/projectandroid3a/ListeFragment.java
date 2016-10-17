package com.example.antoine.projectandroid3a;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class ListeFragment extends Fragment {


    private DataFromHttpRequest tunnel;

    private ListView mListView; // container pour afficher la liste
    private List<PisteReseauCyclable> dataList;
    private ListeAdapter mAdapter;
    private int mMapPermissionGranted;
    private boolean mFavorisAffiches;
    private SharedPreference favoris;


    public ListeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_liste, container, false);

        rootView.setBackgroundColor(Color.WHITE);
        mListView = (ListView)rootView.findViewById(R.id.listeView);
        setListView(rootView);
        favoris = new SharedPreference();

        mMapPermissionGranted = Integer.valueOf(this.getArguments().getString(MainActivity.PERMISSION_MAP));
        mFavorisAffiches = Integer.valueOf(this.getArguments().getString(MainActivity.PRINTING_FAVORITES)) == 1 ? true : false;
        dataList = tunnel.getDataList(); // recuperation de la reference sur la liste des donnees apres la requete http

        tunnel.sendHttpRequestFromFragment();


        List<ListeData> list = new ArrayList<>();
        for(int i = 0; i < dataList.size(); ++i){

            list.add(new ListeData(dataList.get(i).getCompleteStreetNameWithArdt()
                                        , R.drawable.bicycle2));

        }


        mAdapter = new ListeAdapter(getActivity(), list); // affichage de la liste
        mListView.setAdapter(mAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setListView(View rootView) {

        // reponse a un click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position >= 0) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);

                    String detailedData = serializeDataForDetailsActivity(dataList.get(position));
                    intent.putExtra(MainActivity.EXTRA_MESSAGE, detailedData);
                    intent.putExtra(MainActivity.PERMISSION_MAP, Integer.toString(mMapPermissionGranted));


                    getActivity().startActivity(intent);
                }

            }
        });


        mListView.setLongClickable(true);

        // reponse a un click long
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(position >= 0 && mFavorisAffiches) {

                    PisteReseauCyclable data = dataList.get(position);
                    Context context = getActivity().getApplicationContext();
                    final boolean estEnFavori = favoris.isItemInFavorites(data, context);

                    if(estEnFavori){
                        favoris.toastErreur(context, SharedPreference.ERREUR_DEJA_PRESENTE);
                    }
                    else {
                        favoris.toastValide(context);
                        favoris.addFavorite(context, data);
                    }

                }

                if(position >= 0 && !mFavorisAffiches){

                    favoris.removeFavorite(getActivity().getApplicationContext(), dataList.get(position));
                    favoris.toastConfirmationSuppressionFavoris(getActivity().getApplicationContext());
                    ListeData objet = (ListeData) mListView.getItemAtPosition(position);
                    mAdapter.remove(objet);
                    mAdapter.notifyDataSetChanged();

                    if(mAdapter.isEmpty()){
                        ((MainActivity)getActivity()).setErrorMsg(getString(R.string.ERREUR_AUCUN_FAVORI));
                    }

                }

                return true;
            }
        });

    }

     @Override
     public void onAttach(Context context){
         super.onAttach(context);
         tunnel = (DataFromHttpRequest) context;
     }

    private String serializeDataForDetailsActivity(PisteReseauCyclable data){

        Gson gson  = new Gson();

        return gson.toJson(data);

    }
}
