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

    private String HttpRequest;
    private List<PisteReseauCyclable> mDataList;
    private boolean firstRequest;

    public static final int REQUEST_BACK_OFF_MULTIPLIER = 1;
    public static final int REQUEST_TIMEOUT = 15000;
    public static final int REQUEST_NB_RETRY = 1;

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

                        //Log.d("DEB", "Http response");

                        try {


                            JSONArray jsonArray = response.getJSONArray("records");

                            Gson gson = new GsonBuilder().create();

                            for(int i = 0; i < jsonArray.length(); ++i){

                                JSONObject record = jsonArray.getJSONObject(i);
                                String fields = record.getString("fields");

                                PisteReseauCyclable data = gson.fromJson(fields, PisteReseauCyclable.class);

                                getDataList().add(data);

                            }
                            activity.httpRequestReceived(true);

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        //Log.d("DEB", "no Http response");
                        activity.httpRequestReceived(false);

                    }
                });

        // Add the request to the RequestQueue.

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(PisteCyclabeHttpRequestHandler.REQUEST_TIMEOUT
                                                            , PisteCyclabeHttpRequestHandler.REQUEST_NB_RETRY
                                                            , PisteCyclabeHttpRequestHandler.REQUEST_BACK_OFF_MULTIPLIER));

        requestQueue.add(jsObjRequest);
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
