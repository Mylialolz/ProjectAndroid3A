package com.example.antoine.projectandroid3a;

import java.io.Serializable;

/**
 * Created by Antoine on 26/09/2016.
 */

public class UserInput implements Serializable{

    private String keyword;
    private int zipCode;

    public int getArr(){
        return zipCode;
    }

    public void setArr(String input){
        /*Teste si le zipCode est non nul*/
        if(!input.matches("")){
            zipCode = Integer.parseInt(input.toString());
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

}
