package com.example.antoine.projectandroid3a;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {

    public final static String REQUEST = "Editing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(R.layout.activity_launch);

        /*Création d'un bouton de recherche et implémentation d'un listener*/
        Button button = (Button) findViewById(R.id.a_launch_buttonGo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText inputZip = (EditText) findViewById(R.id.a_launch_zipInput);
                EditText inputKeyword = (EditText) findViewById(R.id.a_launch_keyword);
                UserInput searchParameters = new UserInput();
                searchParameters.setArr(inputZip.getText().toString());
                searchParameters.setKeyword(inputKeyword.getText().toString());

                /*Si l'arrondissement est invalide alors prévenir le user*/
                if (searchParameters.getArr() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Arrondissement invalide", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    intent.putExtra(LaunchActivity.REQUEST, searchParameters);
                    startActivity(intent);
                }


            }
        });

    }

}
