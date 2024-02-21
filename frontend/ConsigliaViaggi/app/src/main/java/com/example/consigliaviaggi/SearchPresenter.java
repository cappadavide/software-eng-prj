package com.example.consigliaviaggi;

import android.content.Context;
import android.widget.ListView;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import org.json.JSONObject;
import java.util.HashMap;

public interface SearchPresenter {

    public void getLocationSuggestions(String searchText);
    public void setAdapter(ListView listView);
    public void enableGPS();
    public void createLocationSuggestions(JSONObject response, String term);
    public PermissionsManager getPermissionsManager();
    public boolean checkGPSEnabled();
    public void checkGPSChoiceResult(int requestCode, int resultCode);
    public LatLng getLocation(int position);
    void showError();

    interface ToPresenterRicercaStruttura extends SearchPresenter{

        void research(String localita, String termine, String categoria, String sottocategoria, float rating, String da, String a);
        Context getContext();
        void switchStructureListToView(String[] arraynomi, float[] arrayrating, String[] arrayindirizzo, String[] arrayinfo, int[] arrayid, String[] arrayurl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine);
        void getPlaceName(JSONObject response, Point coords);
        void onDestroyFrag();
    }

    interface ToPresenterMap extends SearchPresenter {

        public void getFacilities(HashMap<String,String> params,LatLng coords);
        //public void createPlaces(JSONObject response);
        public void createPlaces(JSONObject response,LatLng coords);
        public void clearResults();
        void getPlaceName(JSONObject response, Point coords);
        public void onDestroyMap();
        public void onPauseMap();
    }
}

