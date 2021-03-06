package com.example.antoine.projectandroid3a;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antoine on 24/09/2016.
 */

public class PisteCyclabeHttpRequestHandler {


    public static final String TAG_VOLLEY_REQUEST = "TAG_VOLLEY_REQUEST"; // tag pour annuler si besoin les requetes

    private String mHttpRequest; // texte de la requete
    private List<PisteReseauCyclable> mDataList; // liste de donnees recuperees apres la requete
    private boolean mFirstRequest; // boolean pour savoir si la liste de donnees a besoin d'etre effacee avant d'etre actualisee

    private static final int REQUEST_BACK_OFF_MULTIPLIER = 1;
    private static final int REQUEST_TIMEOUT = 7000;
    private static final int REQUEST_NB_RETRY = 1;

    /**
     * Constructeur de la classe qui s'occupe de lancer les requetes http grace a la libraire volley
     * @param HttpRequest - requete http a executer sous forme de chaine de caracteres
     */

    public PisteCyclabeHttpRequestHandler(String HttpRequest){

        // initialisation des donnees membres
        this.setHttpRequest(HttpRequest);
        this.mFirstRequest = true;
        this.mDataList = new ArrayList<>();

    }

    /**
     *
     * @param requestQueue
     * @param activity MainActivity, activity depuis laquelle la requete sera lancee
     */
    public void launchHttpRequest(RequestQueue requestQueue, final MainActivity activity){

        if(isFirstRequest() == false){
            mDataList.clear(); // mise a jour de la liste si nouvelle requete effectuee
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest // fonction appelee lorsque la requete envoyee est recue
                (Request.Method.GET, getHttpRequest(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray jsonArray = response.getJSONArray("records"); // recuperation des infos utiles apres la requete

                            Gson gson = new GsonBuilder().create();

                            for(int i = 0; i < jsonArray.length(); ++i){ // parcours des records

                                JSONObject record = jsonArray.getJSONObject(i); // recuperation d'un tableau parmi tous les recordes
                                String fields = record.getString("fields"); // recuperation des elements tu tableau

                                PisteReseauCyclable data = gson.fromJson(fields, PisteReseauCyclable.class); // json to PisteReseauCyclable

                                getDataList().add(data); // ajout de PisteReseauCyclable dans notre liste

                            }

                            activity.httpRequestReceived(true);

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() { // gestion des erreurs

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        activity.httpRequestReceived(false); // requete trop longue ou qui n'a pas abouti

                    }
                });


        jsObjRequest.setTag(PisteCyclabeHttpRequestHandler.TAG_VOLLEY_REQUEST); // ajout d'un tag pour annulation eventuelle
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(PisteCyclabeHttpRequestHandler.REQUEST_TIMEOUT // temps avant lancement nouvelle requete
                                                            , PisteCyclabeHttpRequestHandler.REQUEST_NB_RETRY // combien de retry
                                                            , PisteCyclabeHttpRequestHandler.REQUEST_BACK_OFF_MULTIPLIER)); // back off multiplier
        requestQueue.add(jsObjRequest); // lancement de la requete
    }


    public String getHttpRequest() {
        return mHttpRequest;
    }

    public void setHttpRequest(String httpRequest) {
        mHttpRequest = httpRequest;
    }

    public List<PisteReseauCyclable> getDataList() {
        return mDataList;
    }

    public boolean isFirstRequest() {
        return mFirstRequest;
    }

}
