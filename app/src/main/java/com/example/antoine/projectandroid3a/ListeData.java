package com.example.antoine.projectandroid3a;

/**
 * Created by Antoine on 23/09/2016.
 * Stocke l'image et le texte des éléments de la liste
 */

public class ListeData {

    private String message;
    private int img;

    public ListeData(String m, int img){

        this.setMessage(m);
        this.setImg(img);

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}

