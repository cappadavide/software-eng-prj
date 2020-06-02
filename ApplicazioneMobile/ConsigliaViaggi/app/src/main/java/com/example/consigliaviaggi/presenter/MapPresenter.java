package com.example.consigliaviaggi.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.consigliaviaggi.PresenterToMapboxAPI;
import com.example.consigliaviaggi.PresenterToViewMap;
import com.example.consigliaviaggi.SearchPresenter.ToPresenterMap;
import com.example.consigliaviaggi.dao.MapboxAPI;
import com.example.consigliaviaggi.presenter.utils.ListAdapter;
import com.example.consigliaviaggi.view.Map;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MapPresenter implements ToPresenterMap, PermissionsListener {

    LocationEngine locationEngine;
    private PresenterToViewMap view;
    private ListAdapter adapter;
    private PresenterToMapboxAPI mapbox;
    private boolean firstTimeGPS;
    private final String MODE;
    private PermissionsManager permissionsManager;
    private ArrayList<LatLng> locations = new ArrayList<>();
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private MapActivityLocationCallback callback;

    public MapPresenter(PresenterToViewMap view, String mode) {

        this.view = view;
        this.MODE = mode;
        firstTimeGPS = true;
        callback = new MapActivityLocationCallback((Map) view.getMapActivity());
        adapter = new ListAdapter(view.getMapActivity(), new ArrayList<String>());
        permissionsManager = new PermissionsManager(this);
        mapbox = new MapboxAPI(this, view.getMapActivity());
    }


    @Override
    public void getLocationSuggestions(String searchText) {

        if (searchText.length() > 2) {
            mapbox.getLocations(searchText);
        } else {
            adapter.clear();
            locations.clear();
            view.hideSuggestionsList();
        }
    }


    @Override
    public void setAdapter(ListView listView) {
        listView.setAdapter(adapter);
    }

    @Override
    public void createLocationSuggestions(JSONObject response, String term) {

        JSONArray p = null;
        adapter.clear();
        locations.clear();
        try {
            p = response.getJSONArray("features");
            for (int i = 0; i < p.length(); i++) {

                JSONObject t;
                JSONArray h;
                t = p.getJSONObject(i);
                adapter.add(t.getString("place_name"));
                h = t.getJSONArray("center");
                locations.add(new LatLng(h.getDouble(1), h.getDouble(0)));
            }
            view.showSuggestionsList();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LatLng getLocation(int position) {
        return locations.get(position);
    }

    @Override
    public void showError() {
        Toast.makeText(view.getMapActivity(), "Controllare lo stato della connessione", Toast.LENGTH_LONG).show();
    }

    @Override
    public void enableGPS() {

        if (PermissionsManager.areLocationPermissionsGranted(view.getMapActivity())) {
            if (!checkGPSEnabled()) {
                turnOnGPS();
            } else initLocationEngine();
        } else permissionsManager.requestLocationPermissions(view.getMapActivity());
    }

    @Override
    public void checkGPSChoiceResult(int requestCode, int resultCode) {

        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {
                initLocationEngine();
                view.showPosition();
            } else view.showEnableGPSToast();
        }
    }

    @Override
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    @Override
    public void getFacilities(HashMap<String, String> params, LatLng coords) {

        if (params.get("indirizzo") == null || Objects.requireNonNull(params.get("indirizzo")).length() == 0)
            Toast.makeText(view.getMapActivity(), "Località non specificata", Toast.LENGTH_SHORT).show();
        else
            mapbox.getFacilities(params, coords);

    }

    /*@Override
    public void createPlaces(JSONObject response) {
        view.showPlaces(response.toString());
    }*/
    public void createPlaces(JSONObject response, LatLng coords) {
        view.showPlaces(response.toString(), coords);
    }

    @Override
    public void clearResults() {
        adapter.clear();
        view.hideSuggestionsList();
    }

    private void getUserPlaceName(Location location) {
        mapbox.getUserPlaceName(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
    }

    @Override
    public void getPlaceName(JSONObject response, Point coords) {

        JSONArray p = null;
        try {
            p = response.getJSONArray("features");
            JSONObject result = p.getJSONObject(0);
            HashMap<String, String> params = new HashMap<>();
            String placeName = result.getString("place_name");
            params.put("indirizzo", placeName);
            params.put("categoria", MODE);
            view.setPlaceName(placeName);
            mapbox.getFacilities(params, null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyMap() {
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
    }

    @Override
    public void onPauseMap() {
        firstTimeGPS = true;
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) turnOnGPS();
    }

    @Override
    public boolean checkGPSEnabled() {

        LocationManager locationManager = (LocationManager) view.getMapActivity().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void turnOnGPS() {

        final Activity activity = view.getMapActivity();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(activity);
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    //Success Perform Task Here
                })
                .addOnFailureListener(e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e("GPS", "Unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("GPS", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                    }
                })
                .addOnCanceledListener(() -> Log.e("GPS", "checkLocationSettings -> onCanceled"));
    }

    private void initLocationEngine() {

        final Activity activity = view.getMapActivity();
        locationEngine = LocationEngineProvider.getBestLocationEngine(activity.getApplicationContext());
        LocationEngineRequest request = new LocationEngineRequest.Builder(mapbox.getInterval())
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(mapbox.getWaitTime()).build();
        if (ActivityCompat.checkSelfPermission(view.getMapActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getMapActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        locationEngine.requestLocationUpdates(request, callback, activity.getMainLooper());
        if (ActivityCompat.checkSelfPermission(view.getMapActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getMapActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        locationEngine.getLastLocation(callback);
    }

    private class MapActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<Map> activityWeakReference;

        MapActivityLocationCallback(Map activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {

            Map activity = activityWeakReference.get();
            if (activity != null && checkGPSEnabled()) {

                Location location = result.getLastLocation();
                if (location == null) return;
                // Pass the new location to the Maps SDK's LocationComponent
                if (!view.isMapBoxNull() && result.getLastLocation() != null) {

                    view.updatePosition(result.getLastLocation());
                    if(firstTimeGPS){
                        getUserPlaceName(result.getLastLocation());
                        firstTimeGPS=false;
                    }
                }
            }
            else if(activity!=null) {

                locationEngine.removeLocationUpdates(this);
                view.hidePosition();
                Toast.makeText(activity,"GPS Disattivato",Toast.LENGTH_LONG).show();
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {

            Log.d("LocationChangeActivity", Objects.requireNonNull(exception.getLocalizedMessage()));
            Map activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}