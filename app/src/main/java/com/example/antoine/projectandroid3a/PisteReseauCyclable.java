package com.example.antoine.projectandroid3a;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Antoine on 23/09/2016.
 */

public class PisteReseauCyclable {

    @Override
    public boolean equals(Object obj) {
        super.equals(obj);
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PisteReseauCyclable other = (PisteReseauCyclable) obj;

        if(other.getGeo_point_2d()[0] != getGeo_point_2d()[0]){
            return false;
        }
        if(other.getGeo_point_2d()[0] != getGeo_point_2d()[0])
            return false;

        return true;
    }

    @SerializedName("bois")
    private String bois;

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

        if(bois == null)
            return "Inconnu";

        if(bois.matches(""))
            return "Inconnu";

        return bois;
    }

    public String getTypologie() {

        if(typologie == null)
            return "Inconnu";

        if(typologie.matches(""))
            return "Inconnu";

        return typologie;
    }

    public int getArrdt() {

        if(arrdt == 0)
            return -1;

        return arrdt;
    }

    public String getStatut() {

        if(statut == null)
            return "Inconnu";

        if(statut.matches(""))
            return "Inconnu";
        return statut;
    }

    public String getSens_velo() {

        if(sens_velo == null)
            return "Inconnu";

        if(sens_velo.matches(""))
            return "Inconnu";
        return sens_velo;
    }

    public String getNom_voie() {

        if(nom_voie == null)
            return "Inconnu";

        if(nom_voie.matches(""))
            return "Inconnu";

        return nom_voie;
    }

    public String getType_voie() {

        if(type_voie == null)
            return "Inconnu";

        if(type_voie.matches(""))
            return "Inconnu";
        return type_voie;
    }

    public String getTypologie_simple() {

        if(typologie_simple == null)
            return "Inconnu";

        if(typologie_simple.matches(""))
            return "Inconnu";
        return typologie_simple;
    }

    public String getShape_len() {
        if(shape_len == null)
            return "Inconnu";
        if(shape_len.matches(""))
            return "Inconnu";
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


}
