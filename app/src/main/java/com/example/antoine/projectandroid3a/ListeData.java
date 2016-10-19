package com.example.antoine.projectandroid3a;

/**
 * Created by Antoine on 23/09/2016.
 * Classe des
 */

public class ListeData {

    private String mMessage;
    private int mImg;

    public ListeData(String m, int img){

        this.setMessage(m);
        this.setImg(img);

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
}

