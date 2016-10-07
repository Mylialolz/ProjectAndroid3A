package com.example.antoine.projectandroid3a;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataFromHttpRequest, TryHttpRequestAgain,TabLayout.OnTabSelectedListener, PermissionErrorInterface {

    public static final String EXTRA_MESSAGE = "ID_ITEM";
    public static final String ITEM_TO_FOCUS_ON = "ITEM_TO_FOCUS_ON";
    public static final String PERMISSION_MAP = "PERMISSION_MAP";
    public static final String PRINTING_FAVORITES = "PRINTING_FAVORITES";
    public static final int PERMISSION_REQUEST_MAP = 1;
    public static final int PERMISSION_REQUEST_INTERNET = 2;


    private String mRequeteHTTP;

    private RequestQueue mRequestQueue;
    private PisteCyclabeHttpRequestHandler httpRequestHandler;

    private final int itemToFocusOnInMap = 0;

    private TabLayout tabLayout;

    private final String[] tabs = {"Liste", "Carte"};

    private boolean permissionMap;
    private boolean permissionInternet;
    private SharedPreference mFavorites;
    private boolean affichageFavoris;
    private List<PisteReseauCyclable> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        UserInput searchParameter = (UserInput)getIntent().getSerializableExtra(LaunchActivity.REQUEST);
        mRequeteHTTP = searchParameter.constructRequest();
        mList = new ArrayList<>();
        mFavorites = new SharedPreference();
        permissionMap = false;
        permissionInternet = false;
        mRequestQueue = Volley.newRequestQueue(this);
        httpRequestHandler = new PisteCyclabeHttpRequestHandler(mRequeteHTTP);
        affichageFavoris = true;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mList.size() != 0)
                    mList.clear();

                if(affichageFavoris) {

                    createLoadingFragment();
                    Snackbar.make(view, "Affichage de vos pistes favories !", Snackbar.LENGTH_LONG).show();
                    ArrayList<PisteReseauCyclable> list = mFavorites.getFavorites(getApplicationContext());
                    for (int i = 0; i < list.size(); ++i) {
                        mList.add(list.get(i));
                    }
                    createListFragment();
                    affichageFavoris = false;
                }
                else {
                    createLoadingFragment();
                    Snackbar.make(view, "Retour aux données courantes", Snackbar.LENGTH_LONG).show();
                    mList = httpRequestHandler.getDataList();
                    createListFragment();
                    affichageFavoris = true;
                }
            }
        });

        
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        for(String str : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(str));
        }
        tabLayout.addOnTabSelectedListener(this);

        askForPermissions();

    }

    private void askForPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                                                    != PackageManager.PERMISSION_GRANTED) {
            askMapPermission();
        }
        else {
            permissionMap = true;
        }


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                                        != PackageManager.PERMISSION_GRANTED) {
            askInternetPermission();
        }
        else {
            permissionInternet = true;
            sendHttpRequest();
        }
    }

    private void askInternetPermission() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Permission accès internet.");
        alertBuilder.setMessage("Nous avons besoin de cette permission pour trouver toutes les pistes cyclables.");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET},
                        PERMISSION_REQUEST_INTERNET);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void askMapPermission() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Permission affichage carte.");
        alertBuilder.setMessage("Nous avons besoin de cette permission pour afficher les éléments sur la carte et lier votre position au GPS.");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_MAP);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int pos = tab.getPosition();
        switch(pos){
            case 0 :
                if(this.getDataList().size() > 0 && permissionInternet == true)
                    this.createListFragment();
                else {
                    if(this.getDataList().size() == 0 && permissionInternet == false)
                        this.createPermissionErrorFragment();
                    if(this.getDataList().size() == 0 && permissionInternet == true)
                        this.createNetworkErrorFragment();
                }
                break;
            case 1 :
                if(this.getDataList().size() > 0 && permissionMap == true)
                    this.createMapFragment();
                else {
                    if(this.getDataList().size() == 0 && permissionMap == false)
                        this.createPermissionErrorFragment();
                    if(this.getDataList().size() == 0 && permissionMap == true)
                        this.createNetworkErrorFragment();
                }
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

    @Override
    public void onResume() {
        super.onResume();

        if(permissionInternet == false && permissionMap == false ){
            this.createPermissionErrorFragment();
        }
    }



    private void sendHttpRequest(){

        if(permissionInternet == true) {
            this.createLoadingFragment();
            final MainActivity activity = this;
            getHttpRequestHandler().launchHttpRequest(mRequestQueue, activity);
        }
        else {
            Toast.makeText(getApplicationContext()
                            , "Impossible de se connecter au serveur."
                            , Toast.LENGTH_LONG).show();
        }

    }


    public void httpRequestReceived(boolean requestReceived){

        if(requestReceived){
            mList = httpRequestHandler.getDataList();
            this.createListFragment();
        }
        else {
            Toast.makeText(getApplicationContext(), "Erreur de réseau.", Toast.LENGTH_LONG).show();
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
        bundle.putString(MainActivity.PRINTING_FAVORITES, Integer.toString(affichageFavoris == true ? 1 : 0));
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

    private void createPermissionErrorFragment(){
        this.manageFragment(new PermissionErrorFragment());
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
        return mList;
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