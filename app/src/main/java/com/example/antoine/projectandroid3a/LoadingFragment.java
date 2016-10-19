package com.example.antoine.projectandroid3a;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 * Crée une roue de chargement
 */
public class LoadingFragment extends Fragment {


    public LoadingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_loading, container, false);

        ProgressBar spinner = (ProgressBar)rootView.findViewById(R.id.progressBar);
        spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF358B") // couleur de la progress bar
                                                            , android.graphics.PorterDuff.Mode.SRC_ATOP);; // mode progress bar

        return rootView;
    }

}
