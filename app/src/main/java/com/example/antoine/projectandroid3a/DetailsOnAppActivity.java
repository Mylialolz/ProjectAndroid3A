package com.example.antoine.projectandroid3a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Diplays all libraries used in the project
 */
public class DetailsOnAppActivity extends AppCompatActivity {

    private static String[] DEPENDENCIES = {
            "En utilisant les librairies suivantes :",
            "com.android.support:appcompat-v7:24.2.0",
            "com.android.support.constraint:constraint-layout:1.0.0-alpha8",
            "com.android.support:support-v4:24.2.0",
            "com.github.bumptech.glide:glide:3.7.0",
            "com.android.volley:volley:1.0.0",
            "com.google.code.gson:gson:2.2.4",
            "com.google.android.gms:play-services:9.4.0",
            "com.android.support:multidex:1.0.1",
            "com.android.support:design:24.2.0"
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_on_app);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView arrayDependencies = (TextView)findViewById(R.id.array_dependencies);
        for (String str : DEPENDENCIES) {
            arrayDependencies.append(str+"\n\n");
        }
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
