package com.example.antoine.projectandroid3a;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Antoine on 23/09/2016.
 */

public class PisteReseauCyclable {

    @SerializedName("bois")
    private String bois;


    @SerializedName("recordid")
    private String recordid;

    @SerializedName("typologie")
    private String typologie;

    @SerializedName("arrdt")
    private int arrdt;

    @SerializedName("statut")
    private String statut;

    @SerializedName("sens_velo")
    private String sens_velo;

    @SerializedName("nom_voie")
    private String nom_voie;

    @SerializedName("type_voie")
    private String type_voie;

    @SerializedName("typologie_simple")
    private String typologie_simple;

    @SerializedName("shape_len")
    private String shape_len;

    @SerializedName("geo_shape")
    private LigneReseauCyclable geo_shape;

    @SerializedName("geo_point_2d")
    private double[] geo_point_2d;


    public PisteReseauCyclable(){

    }

    public String getBois() {
        return bois;
    }

    public String getTypologie() {
        return typologie;
    }

    public int getArrdt() {
        return arrdt;
    }

    public String getStatut() {
        return statut;
    }

    public String getSens_velo() {
        return sens_velo;
    }

    public String getNom_voie() {
        return nom_voie;
    }

    public String getType_voie() {
        return type_voie;
    }

    public String getTypologie_simple() {
        return typologie_simple;
    }

    public String getShape_len() {
        return shape_len;
    }

    public LigneReseauCyclable getGeo_shape() {
        return geo_shape;
    }

    public double[] getGeo_point_2d() {
        return geo_point_2d;
    }


    public String getCompleteStreetName(){

        String a = getType_voie().substring(0,1).toUpperCase();
        String abis = getType_voie().substring(1).toLowerCase();

        String b = getNom_voie().substring(0,1).toUpperCase();
        String bbis = getNom_voie().substring(1).toLowerCase();

        return a + abis + " " + b + bbis;

    }

    public String getCompleteStreetNameWithArdt(){

        String completeStreetName = getCompleteStreetName();

        return completeStreetName + ", " + Integer.toString(this.getArrdt()) + "ème arrondissement";


    }


    public String getDetailedInformationsOnNetworkInHtlmFormat(){

        String lineSep = System.getProperty("line.separator");

        String intro = "<p>Quelques informations complémentaires sur la piste cyclable : </p>" + lineSep;

        String localisation = "<u> Localisation :</u> " + this.getCompleteStreetNameWithArdt() + "." + lineSep;
        String bois = "<u> Zone boisée  :</u> " + this.getBois().toLowerCase() + "." + lineSep;
        String typePiste = "<u> Type de piste :</u> " + this.getTypologie().toLowerCase() + "." + lineSep;
        String sensCirculation = "<u> Sens de circulation :</u> " + this.getSens_velo().toLowerCase() + ".";

        return intro + localisation + bois + typePiste + sensCirculation;

    }


    public String getRecordid() {
        return recordid;
    }
}
