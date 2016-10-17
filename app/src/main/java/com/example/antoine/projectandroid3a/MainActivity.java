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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataFromHttpRequest, TryHttpRequestAgain,TabLayout.OnTabSelectedListener, ErrorInterface {

    public static final String EXTRA_MESSAGE = "ID_ITEM";
    public static final String ITEM_TO_FOCUS_ON = "ITEM_TO_FOCUS_ON";
    public static final String PERMISSION_MAP = "PERMISSION_MAP";
    public static final String PRINTING_FAVORITES = "PRINTING_FAVORITES";
    public static final int PERMISSION_REQUEST_MAP = 1;
    public static final int PERMISSION_REQUEST_INTERNET = 2;
    public static final int TAB_MAP = 1;
    public static final int TAB_LISTE = 0;
    private static String TYPE_MAP = "NORMAL";


    private String mRequeteHTTP;

    private RequestQueue mRequestQueue;
    private PisteCyclabeHttpRequestHandler httpRequestHandler;

    private final int itemToFocusOnInMap = 0;

    private TabLayout tabLayout;

    private final String[] tabs = {"Liste", "Carte"};
    private int currentTab = 0;

    private boolean permissionMap;
    private boolean permissionInternet;
    private SharedPreference mFavorites;
    private boolean affichageFavoris;
    private FloatingActionButton mFab;

    private List<PisteReseauCyclable> mList;
    //private boolean emptyResultFromHttpRequest;

    private String mMsgError ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        UserInput searchParameter = (UserInput)getIntent().getSerializableExtra(LaunchActivity.REQUEST);
        initMemberVariables(searchParameter);

        setFloatingActionButton();
        askForPermissions();

    }

    private void initMemberVariables(UserInput searchParameter) {
        mRequeteHTTP = searchParameter.constructRequest();
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        mList = new ArrayList<>();
        mFavorites = new SharedPreference();
        permissionMap = false;
        //emptyResultFromHttpRequest = true;
        permissionInternet = false;
        mRequestQueue = Volley.newRequestQueue(this);
        httpRequestHandler = new PisteCyclabeHttpRequestHandler(mRequeteHTTP);
        affichageFavoris = true;
    }

    public static String getMapType(){return TYPE_MAP;}

    private void setTabLayout() {
        if(tabLayout.getTabCount() == 0) {
            for (String str : tabs) {
                tabLayout.addTab(tabLayout.newTab().setText(str));
            }
        }
        tabLayout.addOnTabSelectedListener(this);
    }

    private void hideTabLayout(){

        int size = tabLayout.getTabCount();
        if(size > 0) {
            tabLayout.removeAllTabs();
        }

    }



    private void setFloatingActionButton() {
        mFab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mFab.setImageResource(R.drawable.favorite_white);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(affichageFavoris) {
                    affichageFavoris = false;
                    mFab.setImageResource(R.drawable.done_white);
                    createLoadingFragment();
                    Snackbar.make(view, "Affichage de vos pistes favorites !", Snackbar.LENGTH_LONG).show();
                    ArrayList<PisteReseauCyclable> list = mFavorites.getFavorites(getApplicationContext());
                    setTabLayout();
                    mList = list;
                    if(mList.size() > 0)
                        createListFragment();
                    else {
                        setErrorMsg(getString(R.string.ERREUR_AUCUN_FAVORI));
                        createErrorFragment();
                    }
                }
                else {
                    affichageFavoris = true;
                    mFab.setImageResource(R.drawable.favorite_white);
                    createLoadingFragment();
                    Snackbar.make(view, "Retour aux données courantes", Snackbar.LENGTH_LONG).show();
                    mList = httpRequestHandler.getDataList();
                    if(mList.size() > 0)
                        createListFragment();
                    else {
                        sendHttpRequest();
                    }
                }
            }
        });
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
                currentTab = TAB_LISTE;
                mFab.show();
                if(!mList.isEmpty() && permissionInternet == true)
                    this.createListFragment();
                else {
                    if(permissionInternet == false || mList.isEmpty()) {
                        setErrorMsg(getString(R.string.ERREUR_NO_DATA));
                        this.createErrorFragment();
                    }
                }
                break;
            case 1 :
                currentTab = TAB_MAP;
                mFab.hide();
                if(!mList.isEmpty() && permissionMap == true)
                    this.createMapFragment();
                else {
                    if(permissionMap == false ||  !mList.isEmpty()) {
                        setErrorMsg(getString(R.string.ERREUR_NO_DATA));
                        this.createErrorFragment();
                    }
                }
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        return;
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionInternet = true;

                } else {
                    permissionInternet = false;
                }
                return;
            }
            case PERMISSION_REQUEST_MAP: {
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
            setErrorMsg(getString(R.string.ERREUR_PERMISSION_INTERNET));
            createErrorFragment();
        }

    }


    public void httpRequestReceived(boolean requestReceived){

        if(requestReceived){
            mList = httpRequestHandler.getDataList();
            if(!mList.isEmpty()) {
                this.createListFragment();
            }
            else {
                setErrorMsg(getString(R.string.ERREUR_NO_DATA));
                this.createErrorFragment();
            }
            setTabLayout();
        }
        else {
            hideTabLayout();
            Toast.makeText(getApplicationContext()
                                    , "Erreur de réseau."
                                    , Toast.LENGTH_LONG).show();
            this.createNetworkErrorFragment();
        }

        return;
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


    @Override
    public boolean onSupportNavigateUp() {

        switch (currentTab) {
            case TAB_LISTE :
            if (!affichageFavoris) {
                affichageFavoris = true;
                mFab.setImageResource(R.drawable.favorite_white);
                createLoadingFragment();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Retour aux données courantes", Snackbar.LENGTH_LONG).show();
                mList = httpRequestHandler.getDataList();
                if (mList.size() > 0)
                    createListFragment();
                else {
                    sendHttpRequest();
                }
            }
            else {
                mRequestQueue.cancelAll(PisteCyclabeHttpRequestHandler.TAG_VOLLEY_REQUEST);
                finish();
            }
                break;
            case TAB_MAP :
                tabLayout.getTabAt(TAB_LISTE).select();
                break;
        }

        return true;
    }


    private void createNetworkErrorFragment(){
        this.manageFragment(new NetworkErrorFragment());
    }

    private void createErrorFragment(){
        this.manageFragment(new ErrorFragment());
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
        // Handle item selection and select the MAP Type
        int id = item.getItemId();

        switch (id) {
            case R.id.map_typeMap:
                TYPE_MAP = "NORMAL";
                if(currentTab == TAB_MAP){this.createMapFragment();}
                return true;
            case R.id.map_typeEarth:
                TYPE_MAP = "SATELLITE";
                if(currentTab == TAB_MAP){this.createMapFragment();}
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public PisteCyclabeHttpRequestHandler getHttpRequestHandler() {
        return httpRequestHandler;
    }

    @Override
    public List<PisteReseauCyclable> getDataList() {
        return mList;
    }

    @Override
    public void sendHttpRequestFromFragment() {
        this.sendHttpRequest();
    }

    @Override
    public void tryHttpRequestAfterFail() {
        this.sendHttpRequest();
    }

    @Override
    public void onBackPressed(){
        mRequestQueue.cancelAll(PisteCyclabeHttpRequestHandler.TAG_VOLLEY_REQUEST);
        finish();
    }

    public void setErrorMsg(String m){
        this.mMsgError = m;
    }

    @Override
    public String getErrorMsg() {
        return mMsgError;
    }
}