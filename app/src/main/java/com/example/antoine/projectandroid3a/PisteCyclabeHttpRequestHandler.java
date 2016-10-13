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


    public static final String TAG_VOLLEY_REQUEST = "TAG_VOLLEY_REQUEST";

    private String HttpRequest;
    private List<PisteReseauCyclable> mDataList;
    private boolean firstRequest;

    private static final int REQUEST_BACK_OFF_MULTIPLIER = 1;
    private static final int REQUEST_TIMEOUT = 7000;
    private static final int REQUEST_NB_RETRY = 1;

    public PisteCyclabeHttpRequestHandler(String HttpRequest){

        this.setHttpRequest(HttpRequest);
        this.firstRequest = true;
        this.mDataList = new ArrayList<>();

    }

    public void launchHttpRequest(RequestQueue requestQueue, final MainActivity activity){

        if(isFirstRequest() == false){
            mDataList.clear();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, getHttpRequest(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray jsonArray = response.getJSONArray("records"); // recuperation des infos utiles apres la requete

                            Gson gson = new GsonBuilder().create();

                            for(int i = 0; i < jsonArray.length(); ++i){ // parcours des records

                                JSONObject record = jsonArray.getJSONObject(i);
                                String fields = record.getString("fields"); // recuperation des elements de records

                                PisteReseauCyclable data = gson.fromJson(fields, PisteReseauCyclable.class); // json to PisteReseauCyclable

                                getDataList().add(data); // ajout des données dans notre liste

                            }

                            if(getDataList().size() > 0)
                                activity.httpRequestReceived(true, false);
                            else
                                activity.httpRequestReceived(true, true);

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        activity.httpRequestReceived(false, false); // requete trop longue ou qui n'a pas abouti

                    }
                });


        jsObjRequest.setTag(PisteCyclabeHttpRequestHandler.TAG_VOLLEY_REQUEST); // ajout d'un tag pour annulation eventuelle
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(PisteCyclabeHttpRequestHandler.REQUEST_TIMEOUT // temps avant lancement nouvelle requete
                                                            , PisteCyclabeHttpRequestHandler.REQUEST_NB_RETRY // combien de retry
                                                            , PisteCyclabeHttpRequestHandler.REQUEST_BACK_OFF_MULTIPLIER)); // back off multiplier
        requestQueue.add(jsObjRequest); // lancer de la requete
    }


    public String getHttpRequest() {
        return HttpRequest;
    }

    public void setHttpRequest(String httpRequest) {
        HttpRequest = httpRequest;
    }

    public List<PisteReseauCyclable> getDataList() {
        return mDataList;
    }

    public void setDataList(List<PisteReseauCyclable> mDataList) {
        this.mDataList = mDataList;
    }

    public boolean isFirstRequest() {
        return firstRequest;
    }

    public void setFirstRequest(boolean b){
        firstRequest = b;
    }
}
