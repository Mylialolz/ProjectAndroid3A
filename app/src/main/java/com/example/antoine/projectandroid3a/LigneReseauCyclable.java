package com.example.antoine.projectandroid3a;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Antoine on 23/09/2016.
 * Permet de stocker les coordonnées relatives à une piste
 */

public class LigneReseauCyclable {


    @SerializedName("coordinates")
    private double[][] mCoordinates;

    public LigneReseauCyclable(){

    }

    public double[][] getCoordinates() {
        return mCoordinates;
    }
}
