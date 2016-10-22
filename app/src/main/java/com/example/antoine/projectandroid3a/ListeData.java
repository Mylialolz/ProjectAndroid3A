package com.example.antoine.projectandroid3a;

import android.graphics.drawable.Drawable;

/**
 * Created by Antoine on 23/09/2016.
 * Classe des
 */

public class ListeData {

    private String mMessage;
    private int mImg;
    //private String mVoie;
    private Drawable mDrawable;

    public ListeData(String m, Drawable drawable){

        this.setMessage(m);
        this.mDrawable = drawable;

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

    //public void setVoie(String voie){ this.mVoie = voie;}

    //public String getVoie() { return mVoie;}

    public Drawable getDrawable() {
        return mDrawable;
    }

}

