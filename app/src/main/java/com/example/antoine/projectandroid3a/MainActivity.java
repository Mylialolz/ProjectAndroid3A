package com.example.antoine.projectandroid3a;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
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

/*Activity qui s'occupe de l'envoi de la requête, de la récupération de données et de leur mise en forme --> Activity principale de l'application*/
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
    private PisteCyclabeHttpRequestHandler mHttpRequestHandler;

    private final int itemToFocusOnInMap = 0;

    private TabLayout tabLayout;

    private final String[] mTabsForTabLayout = {"Liste", "Carte"};
    private int mCurrentTab = 0;

    private boolean mPermissionMap;
    private boolean mPermissionInternet;
    private boolean mAffichageFavoris;
    private FloatingActionButton mFab;

    private List<PisteReseauCyclable> mList;
    //private boolean emptyResultFromHttpRequest;

    private String mMsgError ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.a_main_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        UserInput searchParameter = (UserInput)getIntent().getSerializableExtra(LaunchActivity.REQUEST);
        initMemberVariables(searchParameter);

        setFloatingActionButton();
        askForPermissions();

    }
    
    /*Prépare la requête HTTP en fonction des paramètres entrés par le User*/
    private void initMemberVariables(UserInput searchParameter) {
        mRequeteHTTP = searchParameter.constructRequest();
        tabLayout = (TabLayout)findViewById(R.id.a_main_tabLayout);
        mList = new ArrayList<>();
        mAffichageFavoris = false;
        //emptyResultFromHttpRequest = true;
        mPermissionInternet = false;
        mRequestQueue = Volley.newRequestQueue(this);
        mHttpRequestHandler = new PisteCyclabeHttpRequestHandler(mRequeteHTTP);
        mAffichageFavoris = true;
    }

    public static String getMapType(){return TYPE_MAP;}

    private void setTabLayout() {
        if(tabLayout.getTabCount() == 0) {
            for (String str : mTabsForTabLayout) {
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


    /*Gère l'affichage des favoris par appui sur le bouton flottant*/
    private void setFloatingActionButton() {
        mFab = (FloatingActionButton) findViewById(R.id.a_main_Fab);
        mFab.setImageResource(R.drawable.favorite_white);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAffichageFavoris) {
                    mAffichageFavoris = false;
                    mFab.setImageResource(R.drawable.done_white);
                    createLoadingFragment();
                    Snackbar.make(view, "Affichage de vos pistes favorites !", Snackbar.LENGTH_LONG).show();
                    ArrayList<PisteReseauCyclable> list = SharedPreference.getFavorites(getApplicationContext());
                    setTabLayout();
                    mList = list;
                    if(mList.size() > 0)
                        createListFragment();
                    else {
                        createErrorFragment(getString(R.string.ERREUR_AUCUN_FAVORI));
                    }
                }
                else {
                    mAffichageFavoris = true;
                    mFab.setImageResource(R.drawable.favorite_white);
                    createLoadingFragment();
                    Snackbar.make(view, "Retour aux données courantes", Snackbar.LENGTH_LONG).show();
                    mList = mHttpRequestHandler.getDataList();
                    if(mList.size() > 0)
                        createListFragment();
                    else {
                        sendHttpRequest();
                    }
                }
            }
        });
    }

    /*Demande à l'OS si on peut accéder à Internet et à la localisation*/
    private void askForPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                                                    != PackageManager.PERMISSION_GRANTED) {
            askMapPermission();
        }
        else {
            mPermissionMap = true;
        }


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                                        != PackageManager.PERMISSION_GRANTED) {
            askInternetPermission();
        }
        else {

            mPermissionInternet = true;
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

    /*Demande la permission d'utiliser la Map*/
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

    /*Gère le passage d'un fragment (ici les tabs Map et Liste) à un autre*/
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int pos = tab.getPosition();
        switch(pos){
            case 0 :
                mCurrentTab = TAB_LISTE;
                mFab.show();
                if(!mList.isEmpty() && mPermissionInternet == true)
                    this.createListFragment();
                else {
                    if(mPermissionInternet == true || mList.isEmpty()) {
                        this.createErrorFragment(getString(R.string.ERREUR_NO_DATA));
                    }
                    if(mPermissionMap == false){
                        this.createErrorFragment("Permission map non accordée. Impossible d'afficher des éléments.");
                    }

                }
                break;
            case 1 :
                mCurrentTab = TAB_MAP;
                mFab.hide();
                if(!mList.isEmpty() && mPermissionMap == true)
                    this.createMapFragment();
                else {
                    if(mPermissionMap == true ||  !mList.isEmpty()) {
                        this.createErrorFragment(getString(R.string.ERREUR_NO_DATA));
                    }
                    if(mPermissionMap == false){
                        this.createErrorFragment("Permission map non accordée. Impossible d'afficher des éléments.");
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

    /* Récupère et stocke les demandes de permissions dans des variables*/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionInternet = true;
                    this.sendHttpRequest();
                } else {
                    mPermissionInternet = false;
                    this.createErrorFragment("Permission internet non accordée. Impossible d'afficher des éléments.");
                }
                return;
            }
            case PERMISSION_REQUEST_MAP: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mPermissionMap = true;

                } else {
                        mPermissionMap = false;
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /*Envoi de la requête HTTP à l'API*/
    private void sendHttpRequest(){

        if(mPermissionInternet == true) {
            this.createLoadingFragment();
            final MainActivity activity = this;
            getHttpRequestHandler().launchHttpRequest(mRequestQueue, activity);
        }
        else {
            Toast.makeText(getApplicationContext()
                                    , "Impossible de se connecter au serveur."
                                    , Toast.LENGTH_LONG).show();
            createErrorFragment(getString(R.string.ERREUR_PERMISSION_INTERNET));
        }

    }

    /*Récupère et stocke les données issues de la requête*/
    public void httpRequestReceived(boolean requestReceived){

        if(requestReceived){
            mList = mHttpRequestHandler.getDataList();
            if(!mList.isEmpty()) {
                this.createListFragment();
            }
            else {
                this.createErrorFragment(getString(R.string.ERREUR_NO_DATA));
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

    /*Animation de chargement*/
    private void createLoadingFragment(){
        this.manageFragment(new LoadingFragment());
    }

    /* Crée un nouveau fragment contenant la liste des résultats*/
    private void createListFragment(){
        ListeFragment list = new ListeFragment();

        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.PERMISSION_MAP, Integer.toString(mPermissionMap == true ? 1 : 0));
        bundle.putString(MainActivity.PRINTING_FAVORITES, Integer.toString(mAffichageFavoris == true ? 1 : 0));
        list.setArguments(bundle);

        this.manageFragment(list);
    }

    /* Crée un nouveau fragment contenant la map*/
    private void createMapFragment(){
        CustomMapFragment mapFragment = new CustomMapFragment();

        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.ITEM_TO_FOCUS_ON, Integer.toString(itemToFocusOnInMap));
        mapFragment.setArguments(bundle);

        this.manageFragment(mapFragment);
    }

    /*Gestion du bouton de Retour*/
    @Override
    public boolean onSupportNavigateUp() {

        switch (mCurrentTab) {
            case TAB_LISTE :
            if (!mAffichageFavoris) {
                mAffichageFavoris = true;
                mFab.setImageResource(R.drawable.favorite_white);
                createLoadingFragment();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Retour aux données courantes", Snackbar.LENGTH_LONG).show();
                mList = mHttpRequestHandler.getDataList();
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

    private void createErrorFragment(String msg){
        this.setErrorMsg(msg);
        this.manageFragment(new ErrorFragment());
    }

    private void manageFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.a_main_displayFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    /*Enregistrement des préférences de l'utilisateur dans les settings*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection and select the MAP Type
        int id = item.getItemId();

        switch (id) {
            case R.id.map_typeMap:
                TYPE_MAP = "NORMAL";
                if(mCurrentTab == TAB_MAP){this.createMapFragment();}
                return true;
            case R.id.map_typeEarth:
                TYPE_MAP = "SATELLITE";
                if(mCurrentTab == TAB_MAP){this.createMapFragment();}
                return true;
            case R.id.details:
                //Intent intent = new Intent(MainActivity.this, DetailsOnAppActivity.class);
                //startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public PisteCyclabeHttpRequestHandler getHttpRequestHandler() {
        return mHttpRequestHandler;
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
