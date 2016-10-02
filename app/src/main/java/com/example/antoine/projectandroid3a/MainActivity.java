package com.example.antoine.projectandroid3a;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataFromHttpRequest, TryHttpRequestAgain,TabLayout.OnTabSelectedListener, PermissionErrorInterface {

    public static final String EXTRA_MESSAGE = "ID_ITEM";
    public static final String ITEM_TO_FOCUS_ON = "ITEM_TO_FOCUS_ON";
    public static final String PERMISSION_MAP = "PERMISSION_MAP";
    public static final int PERMISSION_REQUEST_MAP = 1;
    public static final int PERMISSION_REQUEST_INTERNET = 2;


    private String mRequeteHTTP;

    private RequestQueue mRequestQueue;
    private PisteCyclabeHttpRequestHandler httpRequestHandler;

    private final int itemToFocusOnInMap = 0;
    private boolean erreurReseau;

    private TabLayout tabLayout;

    private final String[] tabs = {"Liste", "Carte"};


    private boolean permissionMap;
    private boolean permissionInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        UserInput searchParameter = (UserInput)getIntent().getSerializableExtra(LaunchActivity.REQUEST);
        mRequeteHTTP = searchParameter.constructRequest();

        erreurReseau = true;
        mRequestQueue = Volley.newRequestQueue(this);
        httpRequestHandler = new PisteCyclabeHttpRequestHandler(mRequeteHTTP);
        
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        for(String str : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(str));
        }
        tabLayout.addOnTabSelectedListener(this);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                                                    != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_MAP);
        }
        else {
            permissionMap = true;
        }


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_INTERNET);
        }
        else {
            permissionInternet = true;
            this.sendHttpRequest();
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int pos = tab.getPosition();
        switch(pos){
            case 0 :
                if(!erreurReseau && permissionInternet == true)
                    this.createListFragment();
                break;
            case 1 :
                if(!erreurReseau
                                && this.getDataList().size() > 0
                                && permissionMap == true)
                    this.createMapFragment();
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionInternet = true;

                } else {
                    permissionInternet = false;
                }
                return;
            }
            case PERMISSION_REQUEST_MAP: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionMap = true;

                } else {
                        permissionMap = false;
                }
                return;
            }
        }
    }



    private void sendHttpRequest(){

        if(permissionInternet == true) {
            this.createLoadingFragment();
            final MainActivity activity = this;
            getHttpRequestHandler().launchHttpRequest(mRequestQueue, activity);
        }
        else {
            Toast.makeText(getApplicationContext(), "Impossible de se connecter au serveur.", Toast.LENGTH_LONG).show();
        }

    }


    public void httpRequestReceived(boolean requestReceived){

        if(requestReceived){
            this.createListFragment();
            erreurReseau = false;
        }
        else {
            Toast.makeText(getApplicationContext(), "Erreur de réseau.", Toast.LENGTH_LONG).show();
            erreurReseau = true;
            this.createNetworkErrorFragment();
        }


    }

    private void createLoadingFragment(){

        this.manageFragment(new LoadingFragment());

    }


    private void createListFragment(){

        ListeFragment list = new ListeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.PERMISSION_MAP, Integer.toString(permissionMap == true ? 1 : 0));
        list.setArguments(bundle);
        this.manageFragment(list);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        switch (id) {
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


    @Override
    public String getPermissionErrorMsg() {
        String msg = "";
        if(permissionInternet == false){
            msg += "Impossible de récupérer les pistes cyclables depuis le serveur (permission non accordée).\n";
        }
        if(permissionMap == false){
            msg += "Impossible de récupérer la localisation des pistes cyclables depuis le serveur (permission non accordée).\n";
        }
        return msg;
    }
}