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

/*Traitement et affichage des données de chaque piste cyclable*/
public class DetailsActivity extends AppCompatActivity implements DataFromHttpRequest, ErrorInterface {

    private final String mLineSep = System.getProperty("line.separator"); // \n

    private PisteReseauCyclable mPiste; // piste dont il faut afficher les détails
    private List<PisteReseauCyclable> mDataListe; // liste avec un et un seul élement dont il faut afficher la position (contrainte imposee par "CustomMapFragment")
    private Menu mMenu; // menu de l'appbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_details);

        this.setTitle("Détails sur la piste");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String dataFromMainActivity = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE); // String sous format JSON qui décrit la piste dont il faut afficher les details
        final int mapPermissionGranted = Integer.valueOf(getIntent().getStringExtra(MainActivity.PERMISSION_MAP)); // check de la permission pour afficher la map

        mPiste =  new Gson().fromJson(dataFromMainActivity, PisteReseauCyclable.class); // String JSON to PisteReseauCyclable

        mDataListe = new ArrayList<>(); // init de la liste pour creer
        mDataListe.add(mPiste);

        setTextWithDetailedInformation(); // affichage des details sur la piste
        initMapFragment(mapPermissionGranted); // charge la map a partir de la liste "mDataListe"

    }
    /*Charge le fragment Map si la permission est accordée*/
    private void initMapFragment(int mapPermissionGranted) {
        if(mapPermissionGranted > 0) {
            CustomMapFragment mapFragment = new CustomMapFragment();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.ITEM_TO_FOCUS_ON, Integer.toString(0));
            mapFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.a_details_mapFragment, mapFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.a_details_mapFragment, new ErrorFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }
    
    /*Met en forme le texte descriptif d'une piste cyclable*/
    private void setTextWithDetailedInformation() {
        TextView txt = (TextView) findViewById(R.id.a_details_infoSup);

        String intro = "Quelques informations complémentaires sur la piste cyclable :" + mLineSep;
        txt.setText(intro);
        txt.append("\n");

        String localisation = "Localisation :";
        SpannableString _localisation = new SpannableString(localisation);
        _localisation.setSpan(new UnderlineSpan(), 0, localisation.length(), 0);
        txt.append(_localisation);
        txt.append(" " + mPiste.getCompleteStreetNameWithArdt() + "." + mLineSep);

        String bois = "Zone boisée  :";
        SpannableString _bois = new SpannableString(bois);
        _bois.setSpan(new UnderlineSpan(), 0, bois.length(), 0);
        txt.append(_bois);
        txt.append(" " + mPiste.getBois().toLowerCase() + "." + mLineSep);

        String typePiste = "Type de piste :";
        SpannableString _typePiste = new SpannableString(typePiste);
        _typePiste.setSpan(new UnderlineSpan(), 0, typePiste.length(), 0);
        txt.append(_typePiste);
        txt.append(" " + mPiste.getTypologie().toLowerCase() + "." + mLineSep);

        String sensCirculation = "Sens de circulation :";
        SpannableString _sensCirculation = new SpannableString(sensCirculation);
        _sensCirculation.setSpan(new UnderlineSpan(), 0, sensCirculation.length(), 0);
        txt.append(_sensCirculation);
        txt.append(" " + mPiste.getSens_velo().toLowerCase() + "." + mLineSep + "\n");

    }


    /*Charge le bouton favori et le met en forme selon l'appartenance de la piste aux favoris*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details_activity, menu);
        super.onCreateOptionsMenu(menu);

        mMenu = menu;
        MenuItem favoritesMenu = mMenu.findItem(R.id.action_favorite);
        final boolean isFavored = SharedPreference.isItemInFavorites(mPiste
                                                                        , getApplicationContext());
        if(isFavored){
            favoritesMenu.setIcon(R.drawable.favored_white);
        }
        else {
            favoritesMenu.setIcon(R.drawable.favorite_white);
        }

        return true;
    }


    /*Gère l'ajout aux Favoris*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite :

                final boolean estFavorite = SharedPreference.isItemInFavorites(mPiste
                                                                                , getApplicationContext());
                if(estFavorite != true){
                    SharedPreference.toastValide(getApplicationContext());
                    SharedPreference.addFavorite(getApplication(), mPiste);
                    mMenu.findItem(R.id.action_favorite).setIcon(R.drawable.favored_white);
                }
                else {
                    SharedPreference.toastErreur(getApplicationContext(), SharedPreference.ERREUR_DEJA_PRESENTE);
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
        return mDataListe;
    }

    @Override
    public void sendHttpRequestFromFragment() {
        return;
    }


    @Override
    public String getErrorMsg() {
        return "Impossible d'afficher la carte associée à cet élément (permission non accordée)";
    }
}
