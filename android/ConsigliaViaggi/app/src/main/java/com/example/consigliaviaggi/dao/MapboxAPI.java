package com.example.consigliaviaggi.dao;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.consigliaviaggi.PresenterToMapboxAPI;
import com.example.consigliaviaggi.SearchPresenter;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class MapboxAPI implements PresenterToMapboxAPI {

    private static final String TOKEN="pk.eyJ1IjoiY2FwcGFkYXZpZGUiLCJhIjoiY2s4eGEzYjBxMDQzcTNscXpqcDRoZHp6ciJ9.AAsoCB4X8U2gQZVRa0U1Bg";
    private static final String TAG = "Map";
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 5000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 2;
    private RequestQueue queue;
    private Context context;
    SearchPresenter presenterMap;

    public MapboxAPI(SearchPresenter presenterMap, Context context){

        this.context=context;
        this.presenterMap=presenterMap;
        queue= Volley.newRequestQueue(context);
    }

    public MapboxAPI(SearchPresenter.ToPresenterRicercaStruttura presenterMap, Context context){

        this.context=context;
        this.presenterMap=presenterMap;
        queue= Volley.newRequestQueue(context);
    }


    @Override
    public void getLocations(final String searchTerm) {

        queue.cancelAll(TAG); //Erase any hanging request
        String url= null;
        try {
            url = "https://api.mapbox.com/geocoding/v5/mapbox.places/"+ URLEncoder.encode(searchTerm, "UTF-8")+".json?autocomplete=true&access_token="+TOKEN+"&types=place&language=it";
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsr = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> presenterMap.createLocationSuggestions(response,searchTerm), error -> {
                    Log.e("Volley","Error");
                    presenterMap.showError();
                });
        jsr.setTag(TAG);
        queue.add(jsr);
    }

    @Override
    public long getInterval() {
        return DEFAULT_INTERVAL_IN_MILLISECONDS;
    }

    @Override
    public long getWaitTime() {
        return DEFAULT_MAX_WAIT_TIME;
    }

    @Override
    public void getFacilities(HashMap<String,String> params, LatLng coords) {

        String url=buildRequest(params);
        JsonObjectRequest jsr = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    try {
                        if(presenterMap instanceof SearchPresenter.ToPresenterMap)
                            ((SearchPresenter.ToPresenterMap) presenterMap).createPlaces(response,coords);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }, error -> {
                    Log.e("Volley","Error");
                    presenterMap.showError();
                });
        queue.add(jsr);
    }

    @Override
    public void getUserPlaceName(Point userPosition) {

        String url="https://api.mapbox.com/geocoding/v5/mapbox.places/";
        url+=userPosition.longitude()+","+userPosition.latitude()+".json?access_token="+TOKEN+"&types=place&language=it";
        JsonObjectRequest jsr = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    try{
                        if(presenterMap instanceof  SearchPresenter.ToPresenterRicercaStruttura)
                            ((SearchPresenter.ToPresenterRicercaStruttura) presenterMap).getPlaceName(response,userPosition);
                        if(presenterMap instanceof SearchPresenter.ToPresenterMap)
                            ((SearchPresenter.ToPresenterMap) presenterMap).getPlaceName(response,userPosition);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }, error -> {
                    Log.e("Volley","Error");
                    presenterMap.showError();
                });
        queue.add(jsr);
    }

    private String buildRequest(HashMap<String,String> params)  {

        String url= "https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=strutture_mappa";
        String categoria=params.get("categoria");
        if(categoria!=null) url=url+"&categoria="+categoria;
        String indirizzo=params.get("indirizzo");

        if(indirizzo!=null) {
            try {
                url=url+"&indirizzo="+URLEncoder.encode(indirizzo,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String rating=params.get("rating");
        if(rating!=null) url+="&rating="+rating;

        String sottocategoria=params.get("sottocategoria");
        if(sottocategoria!=null) url+="&sottocategoria="+sottocategoria;

        String prezzoda=params.get("prezzoda");
        if(prezzoda!=null) url+="&prezzoda="+prezzoda;

        String prezzoa=params.get("prezzoa");
        if(prezzoa!=null) url+="&prezzoa="+prezzoa;

        return url;
    }
}
