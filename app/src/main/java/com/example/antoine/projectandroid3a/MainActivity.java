package com.example.antoine.projectandroid3a;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataFromHttpRequest, TryHttpRequestAgain {

    public static final String EXTRA_MESSAGE = "ID_ITEM";
    public static final String ITEM_TO_FOCUS_ON = "ITEM_TO_FOCUS_ON";
    private String mRequeteHTTP;

    private int resultMapFromDetailsActivity;
    private boolean detailsActivityFinished;

    private RequestQueue mRequestQueue;
    private PisteCyclabeHttpRequestHandler httpRequestHandler;

    private int itemToFocusOnInMap;
    private boolean erreurReseau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        // mRequeteHTTP devrait etre recuperer via un intent
        //mRequeteHTTP = "http://opendata.paris.fr/api/records/1.0/search/?dataset=reseau-cyclable&facet=arrdt&facet=statut&facet=typologie&facet=sens_velo&rows=2";

        UserInput searchParameter = (UserInput)getIntent().getSerializableExtra(LaunchActivity.REQUEST);


        resultMapFromDetailsActivity = -1;
        detailsActivityFinished = false;
        erreurReseau = true;
        itemToFocusOnInMap = 0;
        mRequestQueue = Volley.newRequestQueue(this);
        httpRequestHandler = new PisteCyclabeHttpRequestHandler(mRequeteHTTP);

        this.sendHttpRequest();

    }


    private void sendHttpRequest(){

        this.createLoadingFragment();

        final MainActivity activity = this;
        final int delay = 30000;

        new Thread(){
            @Override
            public void run(){

                getHttpRequestHandler().launchHttpRequest(mRequestQueue, activity);
            }

        }.start();

    }


    public void httpRequestReceived(boolean requestReceived){

        if(requestReceived){
            this.createListFragment();
            erreurReseau = false;
        }
        else {
            erreurReseau = true;
            Toast.makeText(getApplicationContext(), "Erreur de réseau.", Toast.LENGTH_LONG);
            this.createNetworkErrorFragment();
        }


    }

    private void createLoadingFragment(){

        this.manageFragment(new LoadingFragment());

    }


    private void createListFragment(){
        itemToFocusOnInMap = 0;
        this.manageFragment(new ListeFragment());
    }

    private void createMapFragment(){
        CustomMapFragment mapFragment = new CustomMapFragment();

        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.ITEM_TO_FOCUS_ON, Integer.toString(itemToFocusOnInMap));
        mapFragment.setArguments(bundle);

        this.manageFragment(mapFragment);
    }

    private void createNetworkErrorFragment(){
        this.manageFragment(new NetworkErrorFragment());
    }

    private void manageFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DetailsActivity.REQUEST_CODE_DETAILS_ACTIVITY && resultCode == RESULT_OK){

            resultMapFromDetailsActivity = Integer.parseInt(data.getStringExtra(DetailsActivity.RESULT_INTENT_DETAILS_ACTIVITY));
            detailsActivityFinished = true;

        }
    }


    @Override
    public void onResume(){

        super.onResume();

        if(detailsActivityFinished == true && resultMapFromDetailsActivity > 0) {

            ListeFragment fragment = (ListeFragment)getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            itemToFocusOnInMap = fragment.getIndexOfLastItemClicked();
            this.createMapFragment();
            detailsActivityFinished = false;
            resultMapFromDetailsActivity = -1;
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        switch (id) {
            case R.id.action_carte:
                if(!erreurReseau)
                    this.createMapFragment();
                return true;

            case R.id.action_liste :
                if(!erreurReseau)
                    this.createListFragment(); // affichage de la liste
                return true;
            default:case R.id.action_settings :
                return super.onOptionsItemSelected(item);
        }
    }


    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public PisteCyclabeHttpRequestHandler getHttpRequestHandler() {
        return httpRequestHandler;
    }

    @Override
    public List<PisteReseauCyclable> getDataList() {
        return httpRequestHandler.getDataList();
    }


    @Override
    public void tryHttpRequestAfterFail() {

        this.sendHttpRequest();


    }

    @Override
    public void onBackPressed(){
        finish();
    }


}