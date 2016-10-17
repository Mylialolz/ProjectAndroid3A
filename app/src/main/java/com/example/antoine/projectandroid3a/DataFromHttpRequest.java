package com.example.antoine.projectandroid3a;

import java.util.List;

/**
 * Created by Antoine on 24/09/2016.
 */

public interface DataFromHttpRequest {
    List<PisteReseauCyclable> getDataList();

    void sendHttpRequestFromFragment();

}
