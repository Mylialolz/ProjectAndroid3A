package com.example.antoine.projectandroid3a;

import java.util.List;

/**
 * Created by Antoine on 24/09/2016.
   Interface tunnel entre un fragment et l'activité pour lancer une requête API
 */

public interface DataFromHttpRequest {
    List<PisteReseauCyclable> getDataList();

    void sendHttpRequestFromFragment();

}
