package com.example.antoine.projectandroid3a;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by antoine on 29/09/2016.
 * Gère l'ajout et la suppression des Favoris*/

public class SharedPreference {

    public static final String PREF_TAG = "PISTEAPP";
    public static final String FAVORITES = "PISTE_Favorites";
    public static final int ERREUR_DEJA_PRESENTE = 0;
    public static final int ERREUR_INCONNUE = 1;

    public SharedPreference() {
        super();
    }

    public static void saveFavorites(Context context, List<PisteReseauCyclable> favorites) {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = context.getApplicationContext().getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Gson gson = new Gson();
        editor.putString(FAVORITES, gson.toJson(favorites));
        editor.commit();
    }

    public static void addFavorite(Context context, PisteReseauCyclable piste){
        List<PisteReseauCyclable> favorites = getFavorites(context);
        if(favorites == null)
            favorites = new ArrayList<PisteReseauCyclable>();
        favorites.add(piste);
        saveFavorites(context, favorites);
    }

    public static void removeFavorite(Context context, PisteReseauCyclable piste){
        ArrayList<PisteReseauCyclable> favorites = getFavorites(context);
        if(favorites != null){
            favorites.remove(piste);
            saveFavorites(context,favorites);
        }
    }

    public static ArrayList<PisteReseauCyclable> getFavorites(Context context){
        SharedPreferences sharedPref;
        List<PisteReseauCyclable> favorites;

        sharedPref = context.getApplicationContext().getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE);

        if(sharedPref.contains(FAVORITES)){
            String jsonFavorites = sharedPref.getString(FAVORITES, null);
            Gson gson = new Gson();
            PisteReseauCyclable[] favoritesStops = gson.fromJson(jsonFavorites,
                    PisteReseauCyclable[].class);
            favorites = Arrays.asList(favoritesStops);
            favorites = new ArrayList<PisteReseauCyclable>(favorites);
        } else {
            return null;
        }

        return (ArrayList<PisteReseauCyclable>) favorites;
    }


    public static boolean isItemInFavorites(PisteReseauCyclable data, Context context) {

        ArrayList<PisteReseauCyclable> listeFavoris = getFavorites(context);

        boolean isFavored = false;

        if(listeFavoris != null)
            isFavored = checkItem(listeFavoris, data);
        else
            isFavored = false;


        return isFavored;
    }

    private static boolean checkItem(ArrayList<PisteReseauCyclable> list, PisteReseauCyclable data){
        boolean ret = false;
        for(int i = 0; i < list.size(); ++i){
            if(list.get(i).equals(data)) {
                ret = true;
                return ret;
            }
        }
        return ret;
    }

    public static void toastValide(Context context){
        Toast.makeText(context
                        , "La piste a été ajoutée en tant que favorite"
                        , Toast.LENGTH_LONG).show();
    }


    public static void toastErreur(Context context, int codeErreur){

        switch (codeErreur)
        {
            case SharedPreference.ERREUR_DEJA_PRESENTE :
                    Toast.makeText(context
                                    , "La piste figure déjà parmi les pistes favorites"
                                    , Toast.LENGTH_LONG).show();
                break;

            case SharedPreference.ERREUR_INCONNUE :
                Toast.makeText(context
                                    , "La piste n'a pas pu être ajoutée"
                                    , Toast.LENGTH_LONG).show();

                break;
        }
        return;
    }


    public static void toastConfirmationSuppressionFavoris(Context context){
        Toast.makeText(context
                , "Piste favorite supprimée"
                , Toast.LENGTH_LONG).show();
    }



}
