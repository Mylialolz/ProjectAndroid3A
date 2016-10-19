package com.example.antoine.projectandroid3a;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends Fragment {

    private ErrorInterface listener; // tunnel de communication entre l'activité et le fragment. Permet de recuperer le message d'erreur

    public ErrorFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_error, container, false);

        String msg = listener.getErrorMsg(); // recuperation du message d'erreur
        TextView txt = (TextView)rootView.findViewById(R.id.f_error_TxtError);
        txt.setText(msg); // affichage du message d'erreur

        return rootView;
    }


    @Override
    public void onAttach(Context context){ // creation du tunnel de communication
        super.onAttach(context);
        listener = (ErrorInterface) context;
    }

}
