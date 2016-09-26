package com.example.antoine.projectandroid3a;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class NetworkErrorFragment extends Fragment {

    private TryHttpRequestAgain listener;


    public NetworkErrorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_network_error, container, false);


        Button button = (Button)rootView.findViewById(R.id.tryAgain);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                listener.tryHttpRequestAfterFail();
            }
        });


        // Inflate the layout for this fragment
        return  rootView;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (TryHttpRequestAgain) context;
    }


}
