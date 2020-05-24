package com.example.consigliaviaggi.view;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;


public class Map extends AppCompatActivity{

    private static final String TOKEN="pk.eyJ1IjoiY2FwcGFkYXZpZGUiLCJhIjoiY2s4eGEzYjBxMDQzcTNscXpqcDRoZHp6ciJ9.AAsoCB4X8U2gQZVRa0U1Bg";
    private static final String CV_STYLE="mapbox://styles/cappadavide/ck99zp7l50tj11inz8mjplg6m";
    private MapView mapView;
    private MapboxMap mapBoxMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);


    }


    @Override
    public void onStart() {

        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {

        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {

        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }





}
