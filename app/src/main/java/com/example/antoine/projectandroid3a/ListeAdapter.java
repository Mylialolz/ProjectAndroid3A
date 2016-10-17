package com.example.antoine.projectandroid3a;

import android.content.Context;
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

        TextView txtV = (TextView)itemV.findViewById(R.id.itemTxt);
        ImageView imgV = (ImageView) itemV.findViewById(R.id.itemImg);

        ListeData sampleData = getItem(position);

        txtV.setText(sampleData.getMessage());
        imgV.setImageResource(sampleData.getImg());

        return itemV;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
