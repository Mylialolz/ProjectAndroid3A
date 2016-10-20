package com.example.antoine.projectandroid3a;

/**
 * Created by Antoine on 23/09/2016.
 * Classe des
 */

public class ListeData {

    private String mMessage;
    private int mImg;
    private String mVoie;

    public ListeData(String m, int img, String nomVoie){

        this.setMessage(m);
        this.setImg(img);
        this.mVoie = nomVoie;

    }


    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public int getImg() {
        return mImg;
    }

    public void setImg(int img) {
        this.mImg = img;
    }

    public void setVoie(String voie){ this.mVoie = voie;}

    public String getVoie() { return mVoie;}

}

