package com.example.antoine.projectandroid3a;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/*Fragment qui gère l'affichage de la ListView contenant les données reçues*/
public class ListeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private DataFromHttpRequest mTunnel;

    private ListView mListView; // container pour afficher la liste
    private List<PisteReseauCyclable> mDataListe;
    private ListeAdapter mAdapter;

    private int mMapPermissionAccordee; // boolean pour savoir s'il est possible d'afficher la map en fonction de la permission acoordée par l'utilisation

    private boolean mEtatNFavorisAffiches; // boolean pour savoir si la liste a afficher est la liste des favoris ou les donnes issues de la requete http


    public ListeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_liste, container, false);

        rootView.setBackgroundColor(Color.WHITE);
        mListView = (ListView)rootView.findViewById(R.id.f_Liste_listView);
        setListView(rootView);


        mMapPermissionAccordee = Integer.valueOf(this.getArguments().getString(MainActivity.PERMISSION_MAP));
        mEtatNFavorisAffiches = Integer.valueOf(this.getArguments().getString(MainActivity.PRINTING_FAVORITES)) == 1 ? true : false;
        mDataListe = mTunnel.getDataList(); // recuperation de la reference sur la liste des donnees apres la requete http

        List<ListeData> list = new ArrayList<>();
        for(PisteReseauCyclable e : mDataListe){

            /** recuperation de la couleur a appliquer selon la voie (rue, boulevard, avenue, etc.) */
            int color = Color.parseColor(ColorHandler.selectColorFilterBasedOnVoie(getActivity().getApplicationContext() // contexte
                                                                                        , e.getType_voie())); // recuperation voie

            Drawable drawable = DrawableHandler.setDrawableFromRessourcesAndApplyColorFilter(getActivity().getApplicationContext()
                                                                                                , R.drawable.ic_location_on_black_48dp_white
                                                                                                , color);
            list.add(new ListeData(e.getCompleteStreetNameWithArdt()
                                                        , drawable));

        }

        mAdapter = new ListeAdapter(getActivity(), list); // affichage de la liste
        mListView.setAdapter(mAdapter);

        SwipeRefreshLayout swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.f_Liste_swipeRefresh);
        if(mEtatNFavorisAffiches == true) { // Autorisation du swipe seulement si les favoris ne sont pas affiches. Le swipe n'a pas d'utilité pour les favoris (aucune raison de rafaichir la liste)

            swipe.setOnRefreshListener(ListeFragment.this);
        }
        else {
            swipe.setEnabled(false);
        }

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

                    String detailedData = serializeDataForDetailsActivity(mDataListe.get(position));
                    intent.putExtra(MainActivity.EXTRA_MESSAGE, detailedData);
                    intent.putExtra(MainActivity.PERMISSION_MAP, Integer.toString(mMapPermissionAccordee));


                    getActivity().startActivity(intent);
                }

            }
        });


        mListView.setLongClickable(true);

        // reponse a un click long
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(position >= 0 && mEtatNFavorisAffiches) {

                    PisteReseauCyclable data = mDataListe.get(position);
                    Context context = getActivity().getApplicationContext();
                    final boolean estEnFavori = SharedPreference.isItemInFavorites(data, context);

                    if(estEnFavori){
                        SharedPreference.toastErreur(context, SharedPreference.ERREUR_DEJA_PRESENTE);
                    }
                    else {
                        SharedPreference.toastValide(context);
                        SharedPreference.addFavorite(context, data);
                    }

                }

                if(position >= 0 && !mEtatNFavorisAffiches){

                    SharedPreference.removeFavorite(getActivity().getApplicationContext(), mDataListe.get(position));
                    SharedPreference.toastConfirmationSuppressionFavoris(getActivity().getApplicationContext());
                    ListeData objet = (ListeData) mListView.getItemAtPosition(position);
                    mAdapter.remove(objet);
                    mAdapter.notifyDataSetChanged();

                   /* if(mAdapter.isEmpty()){
                        ((MainActivity)getActivity()).setErrorMsg(getString(R.string.ERREUR_AUCUN_FAVORI));
                    }*/

                }

                return true;
            }
        });

    }

     @Override
     public void onAttach(Context context){
         super.onAttach(context);
         mTunnel = (DataFromHttpRequest) context;
     }

    private String serializeDataForDetailsActivity(PisteReseauCyclable data){
        Gson gson  = new Gson();
        return gson.toJson(data);

    }

    @Override
    public void onRefresh() {
        mTunnel.sendHttpRequestFromFragment();
    }
}
