package com.example.antoine.projectandroid3a;

import java.io.Serializable;

/**
 * Created by Antoine on 26/09/2016.
 * Stocke et met en forme les paramètres de recherche entrés par l'utilisateur
 * Construit la requête HTTP associée
 */

public class UserInput implements Serializable{

    private String keyword;
    private int zipCode;
    private static final String requestFirstPart = "http://opendata.paris.fr/api/records/1.0/search/?dataset=reseau-cyclable&rows=3500&q=";
    private static final String requestLastPart = "&facet=arrdt&facet=statut&facet=typologie&facet=sens_velo&refine.arrdt=";

    public int getArr(){
        return zipCode;
    }

    public void setArr(String input){
        /*Teste si le zipCode est non nul*/
        if(!input.matches("")){
            zipCode = Integer.parseInt(input);
        }
        else{
            zipCode = 0;
        }

        /*Teste si le numéro de l'arrondissement existe*/
        if(!(zipCode >= 1 && zipCode <= 20)){
            zipCode = 0;
        }
    }

    public String getKeyword(){
        return keyword;
    }

    public void setKeyword(String input){
        keyword = input;
    }

    public String constructRequest(){
        String request = requestFirstPart+keyword+requestLastPart+String.valueOf(zipCode);
        return request;
    }

}
