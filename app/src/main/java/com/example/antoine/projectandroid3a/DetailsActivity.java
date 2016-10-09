package com.example.antoine.projectandroid3a;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements DataFromHttpRequest, ErrorInterface {

    public static final String RESULT_INTENT_DETAILS_ACTIVITY = "VOIR_MAP";
    public static final int REQUEST_CODE_DETAILS_ACTIVITY = 1;
    public static final String PREFS_FILE = "PREF";
    public static final String PREFS_KEY ="RECORD";

    public final String lineSep = System.getProperty("line.separator");

    private List<PisteReseauCyclable> list;
    private PisteReseauCyclable mPiste;
    private Menu mMenu;
    private final SharedPreference favorites = new SharedPreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_details);
        this.setTitle("Détails sur la piste");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String dataFromMainActivity = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        final int mapPermissionGranted = Integer.valueOf(getIntent().getStringExtra(MainActivity.PERMISSION_MAP));

        Gson gson = new Gson();
        mPiste = gson.fromJson(dataFromMainActivity, PisteReseauCyclable.class);
        list = new ArrayList<>();
        list.add(mPiste);
        setTextWithDetailedInformation();

        initMapFragment(mapPermissionGranted);


    }

    private void initMapFragment(int mapPermissionGranted) {
        if(mapPermissionGranted > 0) {
            CustomMapFragment mapFragment = new CustomMapFragment();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.ITEM_TO_FOCUS_ON, Integer.toString(0));
            mapFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.detailFragment, mapFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.detailFragment, new ErrorFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

    private void setTextWithDetailedInformation() {
        TextView txt = (TextView) findViewById(R.id.textDetails);

        String intro = "Quelques informations complémentaires sur la piste cyclable :" + lineSep;
        txt.setText(intro);
        txt.append("\n");

        String localisation = "Localisation :";
        SpannableString _localisation = new SpannableString(localisation);
        _localisation.setSpan(new UnderlineSpan(), 0, localisation.length(), 0);
        txt.append(_localisation);
        txt.append(" " + mPiste.getCompleteStreetNameWithArdt() + "." + lineSep);

        String bois = "Zone boisée  :";
        SpannableString _bois = new SpannableString(bois);
        _bois.setSpan(new UnderlineSpan(), 0, bois.length(), 0);
        txt.append(_bois);
        txt.append(" " + mPiste.getBois().toLowerCase() + "." + lineSep);

        String typePiste = "Type de piste :";
        SpannableString _typePiste = new SpannableString(typePiste);
        _typePiste.setSpan(new UnderlineSpan(), 0, typePiste.length(), 0);
        txt.append(_typePiste);
        txt.append(" " + mPiste.getTypologie().toLowerCase() + "." + lineSep);

        String sensCirculation = "Sens de circulation :";
        SpannableString _sensCirculation = new SpannableString(sensCirculation);
        _sensCirculation.setSpan(new UnderlineSpan(), 0, sensCirculation.length(), 0);
        txt.append(_sensCirculation);
        txt.append(" " + mPiste.getSens_velo().toLowerCase() + "." + lineSep + "\n");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details_activity, menu);
        super.onCreateOptionsMenu(menu);

        mMenu = menu;
        MenuItem favoritesMenu = mMenu.findItem(R.id.action_favorite);
        final boolean isFavored = favorites.isItemInFavorites(mPiste, getApplicationContext());
        if(isFavored){
            favoritesMenu.setIcon(R.drawable.favored_white);
        }
        else {
            favoritesMenu.setIcon(R.drawable.favorite_white);
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite :

                final boolean isFavored = favorites.isItemInFavorites(mPiste, getApplicationContext());
                if(isFavored != true){
                    favorites.toastValide(getApplicationContext());
                    favorites.addFavorite(getApplication(), mPiste);
                    mMenu.findItem(R.id.action_favorite).setIcon(R.drawable.favored_white);
                }
                else {
                    favorites.toastErreur(getApplicationContext(), SharedPreference.ERREUR_DEJA_PRESENTE);
                }


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        finish();

    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public List<PisteReseauCyclable> getDataList() {
        return list;
    }


    @Override
    public String getErrorMsg() {
        return "Impossible d'afficher la carte associée à cet élément (permission non accordée)";
    }
}
