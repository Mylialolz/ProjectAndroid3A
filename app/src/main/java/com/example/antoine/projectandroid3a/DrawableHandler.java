package com.example.antoine.projectandroid3a;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

/**
 * Created by Antoine on 22/10/2016.
 */

public class DrawableHandler {

    public DrawableHandler(){}


    public static Drawable setDrawableFromRessourcesAndApplyColorFilter(Context context, int id, int color){
        Drawable drawable = context.getResources().getDrawable(id);
        drawable.mutate();
        drawable.setColorFilter(color // couleur
                                    , PorterDuff.Mode.SRC_ATOP); // mode de filtrage
        return drawable;
    }

}
