package com.example.consigliaviaggi;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;
import com.mapbox.geojson.Point;

public interface PresenterToViewRicercaStruttura {

    void showSuggestionsList();
    void hideSuggestionsList();
    ListView getListView();
    Context getContext();
    Activity getFragActivity();
    void shareStructureArrayListWithListaStruttura(String[] arraynomi, float[] arrayrating, String[] arrayindirizzo, String[] arrayinfo, int[] arrayid, String[] arrayurl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine);
    void showRangeOption();
    void setLocalita(String placename, Point coords);
}

