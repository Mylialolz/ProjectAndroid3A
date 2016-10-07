package com.example.antoine.projectandroid3a;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by coren on 29/09/2016.
 */

public class SharedPreference {

    public static final String PREF_TAG = "OUIAPP";
    public static final String FAVORITES = "OuiStops_Favorites";

    public SharedPreference() {
        super();
    }

    public void saveFavorites(Context context, List<PisteReseauCyclable> favorites) {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        sharedPref = context.getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Gson gson = new Gson();
        editor.putString(FAVORITES, gson.toJson(favorites));
        editor.commit();
    }

    public void addFavorite(Context context, PisteReseauCyclable stop){
        List<PisteReseauCyclable> favorites = getFavorites(context);
        if(favorites == null)
            favorites = new ArrayList<PisteReseauCyclable>();
        favorites.add(stop);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, PisteReseauCyclable stop){
        ArrayList<PisteReseauCyclable> favorites = getFavorites(context);
        if(favorites != null){
            favorites.remove(stop);
            saveFavorites(context,favorites);
        }
    }

    public ArrayList<PisteReseauCyclable> getFavorites(Context context){
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        List<PisteReseauCyclable> favorites;

        sharedPref = context.getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

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
}
