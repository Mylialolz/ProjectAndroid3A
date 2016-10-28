package com.example.antoine.projectandroid3a;

import android.graphics.drawable.Drawable;

/**
 * Created by Antoine on 23/09/2016.
 * Classe des
 */

public class ListeData {

    private String mMessage;
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

    public Drawable getDrawable() {
        return mDrawable;
    }

}

