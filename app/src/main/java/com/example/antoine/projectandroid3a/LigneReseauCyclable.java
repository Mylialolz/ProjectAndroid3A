package com.example.antoine.projectandroid3a;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Antoine on 23/09/2016.
 */

public class LigneReseauCyclable {


    @SerializedName("coordinates")
    private double[][] coordinates;


    public LigneReseauCyclable(){

    }


    public double[][] getCoordinates() {
        return coordinates;
    }
}
