package com.example.antoine.projectandroid3a;

import java.util.List;

/**
 * Created by Antoine on 17/10/2016.
 * Filtre les résultats pour n'afficher qu'un marqueur pour chaque rue comportant une ou plusieurs pistes cyclables
 */

public class SearchInList {

    public SearchInList(){}

    public static <T> boolean contains(List<T> array, final T v)
    { for (final T e : array) { if (e == v || v != null && v.equals(e)) { return true; } } return false; }


}
