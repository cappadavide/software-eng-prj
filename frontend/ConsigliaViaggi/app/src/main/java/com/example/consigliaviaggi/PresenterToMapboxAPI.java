package com.example.consigliaviaggi;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;

public interface PresenterToMapboxAPI {

    void getLocations(String searchTerm);
    long getInterval();
    long getWaitTime();
    //void getFacilities(HashMap<String,String> params);
    void getFacilities(HashMap<String,String> params, LatLng coords);
    void getUserPlaceName(Point userPosition);

}
