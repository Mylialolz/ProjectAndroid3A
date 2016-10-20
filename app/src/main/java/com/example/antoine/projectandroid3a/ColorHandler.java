package com.example.antoine.projectandroid3a;

import android.widget.ImageView;

import java.util.Random;

/**
 * Created by Antoine on 20/10/2016.
 */

public class ColorHandler {

    private static int TOP_BOUND = 5;

    public ColorHandler(){}

    public static int getRandomColor(){
        Random r = new Random();
        return (r.nextInt(TOP_BOUND) + 1);
    }


    public static void applyFilter(ImageView imageView, int color){
        if(color > 0 && color <= TOP_BOUND) {

            int _color = 1;
            switch (color){
                case 1 :
                    _color = R.color.colorAlea1;
                    break;
                case 2 :
                    _color = R.color.colorAlea2;
                    break;
                case 3 :
                    _color = R.color.colorAlea3;
                    break;
                case 4 :
                    _color = R.color.colorAlea4;
                    break;
                case 5 :
                    _color = R.color.colorAlea5;
                    break;
                default:break;
            }
            imageView.setColorFilter(_color);
        }
    }

    public static String selectColorFilterBasedOnVoie(String voie){
        String _color = "#110EE4";
        if(voie != null) {
            switch (voie){
                case "BOULEVARD" :case "BOULEVARD DE" :
                    _color = "#110EE4";
                    break;
                case "RUE" :case "RUE DE" :
                    _color = "#ff4081";
                    break;
                case "PLACE" :case "PLACE DE" :
                    _color = "#0E9CE4";
                    break;
                case "QUAI" :  case "QUAI DE" :
                    _color = "#0AEA19";
                    break;
                case "AVENUE" : case "AVENUE DE" : case "AVENUE DU" :
                    _color = "#AB9D04";
                    break;
                default:break;
            }
        }
        return _color;
    }

}
