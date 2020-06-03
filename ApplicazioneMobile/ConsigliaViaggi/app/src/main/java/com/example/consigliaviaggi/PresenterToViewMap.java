package com.example.consigliaviaggi;

import android.app.Activity;
import android.location.Location;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import java.util.List;

public interface PresenterToViewMap {
    void showSuggestionsList();
    MapboxMap getMapboxMap();
    void showPlaces(String response, LatLng coords);
    void hideSuggestionsList();
    void showEnableGPSToast();
    void setPlaceName(String placeName);
    void showPosition();
    boolean isMapBoxNull();
    void hidePosition();
    void updatePosition(Location lastLocation);
    Activity getMapActivity();
}

