package com.example.antoine.projectandroid3a;

import android.content.Context;
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

    public static String selectColorFilterBasedOnVoie(Context context, String voie){
        String _color = context.getResources().getString(R.color.colorAlea4);
        if(voie != null) {
            char c = voie.charAt(0);
            switch (c){
                case 'B' :
                    _color = context.getResources().getString(R.color.colorAlea1);
                    break;
                case 'R' :
                    _color = context.getResources().getString(R.color.colorAlea2);
                    break;
                case 'P' :
                    _color = context.getResources().getString(R.color.colorAlea3);
                    break;
                case 'Q' :
                    _color = context.getResources().getString(R.color.colorAlea4);
                    break;
                case 'A' :
                    _color = context.getResources().getString(R.color.colorAlea5);
                    break;
                default:break;
            }
        }
        return new String("#" + _color.substring(3).toUpperCase());
    }

}
