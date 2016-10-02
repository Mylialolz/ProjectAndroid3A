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
public class PermissionErrorFragment extends Fragment {

    private PermissionErrorInterface listener;

    public PermissionErrorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_permission_error, container, false);

        String msg = listener.getPermissionErrorMsg();

        TextView txt = (TextView)rootView.findViewById(R.id.permTxtError);
        txt.setText(msg);

        return rootView;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (PermissionErrorInterface) context;
    }

}
