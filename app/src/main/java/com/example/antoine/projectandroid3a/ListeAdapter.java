package com.example.antoine.projectandroid3a;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Antoine on 23/09/2016.
 * Met en place les données de la piste dans les items de la ListView
 */

public class ListeAdapter extends ArrayAdapter<ListeData>  {


    public ListeAdapter(Context context, List<ListeData> objects){

        super(context, 0, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View itemV = LayoutInflater.from(getContext()).inflate(R.layout.itemlist, parent, false);

        TextView txtV = (TextView)itemV.findViewById(R.id.liste_itemTxt);
        ImageView imgV = (ImageView) itemV.findViewById(R.id.liste_itemImg);

        ListeData sampleData = getItem(position);

        txtV.setText(sampleData.getMessage());


        /** Ne fonctionne pas
         * Devait recolorer l'image en fonction de la voie
         * */
        Drawable drawable = getContext().getResources().getDrawable(sampleData.getImg());
        //Log.d("COLOR", ColorHandler.selectColorFilterBasedOnVoie(sampleData.getVoie()));
        int color = Color.parseColor(ColorHandler.selectColorFilterBasedOnVoie(sampleData.getVoie()));
        //Log.d("COLOR", ColorHandler.selectColorFilterBasedOnVoie(sampleData.getVoie()));
        drawable.setColorFilter(color
                                     , PorterDuff.Mode.SRC_ATOP);
        imgV.setImageDrawable(drawable);
        //Log.d("COLOR", "ok");


        return itemV;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
