package com.example.antoine.projectandroid3a;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

public class MainActivity extends AppCompatActivity implements DataFromHttpRequest, TryHttpRequestAgain,TabLayout.OnTabSelectedListener {

    public static final String EXTRA_MESSAGE = "ID_ITEM";
    public static final String ITEM_TO_FOCUS_ON = "ITEM_TO_FOCUS_ON";
    private String mRequeteHTTP;

    private RequestQueue mRequestQueue;
    private PisteCyclabeHttpRequestHandler httpRequestHandler;

    private final int itemToFocusOnInMap = 0;
    private boolean erreurReseau;

    private TabLayout tabLayout;

    private final String[] tabs = {"Liste", "Carte"};

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

        this.sendHttpRequest();

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int pos = tab.getPosition();
        switch(pos){
            case 0 :
                if(!erreurReseau)
                    this.createListFragment();
                break;
            case 1 :
                if(!erreurReseau)
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


    private void sendHttpRequest(){

        this.createLoadingFragment();
        final MainActivity activity = this;
        getHttpRequestHandler().launchHttpRequest(mRequestQueue, activity);

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


}