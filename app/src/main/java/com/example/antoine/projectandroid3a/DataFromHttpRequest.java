package com.example.antoine.projectandroid3a;

import java.util.List;

/**
 * Created by Antoine on 24/09/2016.
   Interface tunnel entre un fragment et l'activité pour lancer une requête API
 */

public interface DataFromHttpRequest {

    List<PisteReseauCyclable> getDataList(); // recuperation des donnes issues de la requete http sous la forme d'une liste

    void sendHttpRequestFromFragment(); // relancer la requete http depuis un fragment

}
