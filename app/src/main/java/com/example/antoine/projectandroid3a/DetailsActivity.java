package com.example.antoine.projectandroid3a;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements DataFromHttpRequest {

    public static final String RESULT_INTENT_DETAILS_ACTIVITY = "VOIR_MAP";
    public static final int REQUEST_CODE_DETAILS_ACTIVITY = 1;

    public final String lineSep = System.getProperty("line.separator");

    private int voirMap;
    private List<PisteReseauCyclable> list;

    private PisteReseauCyclable mPiste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_details);
        this.setTitle("Détails sur la piste");

        voirMap = 0;
        String dataFromMainActivity = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);

        Gson gson = new Gson();
        mPiste = gson.fromJson(dataFromMainActivity, PisteReseauCyclable.class);

        list = new ArrayList<>();
        list.add(mPiste);

        CustomMapFragment mapFragment = new CustomMapFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.ITEM_TO_FOCUS_ON, Integer.toString(0));
        mapFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.detailFragment, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        setTextWithDetailedInformation();

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
        txt.append(" " + mPiste.getCompleteStreetNameWithArdt() + "." + lineSep + "\n");

        String bois = "Zone boisée  :";
        SpannableString _bois = new SpannableString(bois);
        _bois.setSpan(new UnderlineSpan(), 0, bois.length(), 0);
        txt.append(_bois);
        txt.append(" " + mPiste.getBois().toLowerCase() + "." + lineSep + "\n");

        String typePiste = "Type de piste :";
        SpannableString _typePiste = new SpannableString(typePiste);
        _typePiste.setSpan(new UnderlineSpan(), 0, typePiste.length(), 0);
        txt.append(_typePiste);
        txt.append(" " + mPiste.getTypologie().toLowerCase() + "." + lineSep + "\n");

        String sensCirculation = "Sens de circulation :";
        SpannableString _sensCirculation = new SpannableString(sensCirculation);
        _sensCirculation.setSpan(new UnderlineSpan(), 0, sensCirculation.length(), 0);
        txt.append(_sensCirculation);
        txt.append(" " + mPiste.getSens_velo().toLowerCase() + ".");
    }


    private void finishActivity() {

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        resultIntent.putExtra(this.RESULT_INTENT_DETAILS_ACTIVITY, Integer.toString(voirMap));
        finish();
    }

    @Override
    public void onBackPressed() {

        voirMap = 0;

        finishActivity();

    }

    @Override
    public List<PisteReseauCyclable> getDataList() {
        return list;
    }

}
