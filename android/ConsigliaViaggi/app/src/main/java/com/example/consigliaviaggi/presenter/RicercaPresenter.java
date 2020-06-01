package com.example.consigliaviaggi.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.example.consigliaviaggi.PresenterToMapboxAPI;
import com.example.consigliaviaggi.PresenterToStrutturaDAO;
import com.example.consigliaviaggi.PresenterToViewRicercaStruttura;
import com.example.consigliaviaggi.SearchPresenter;
import com.example.consigliaviaggi.dao.MapboxAPI;
import com.example.consigliaviaggi.dao.StrutturaDAO;
import com.example.consigliaviaggi.presenter.utils.ListAdapter;
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
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class RicercaPresenter implements SearchPresenter.ToPresenterRicercaStruttura, PermissionsListener {

    private LocationEngine locationEngine;
    private PresenterToViewRicercaStruttura view;
    private PresenterToStrutturaDAO dao;
    private ListAdapter adapter;
    private PermissionsManager permissionsManager;
    private ArrayList<LatLng> locations = new ArrayList<>();
    private PresenterToMapboxAPI mapbox;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private MapActivityLocationCallback callback;

    public RicercaPresenter(PresenterToViewRicercaStruttura view, Activity activity) {

        if(view!=null && activity!=null){

            this.view = view;
            dao = new StrutturaDAO(this);
            adapter = new ListAdapter(view.getFragActivity(), new ArrayList<>());
            mapbox = new MapboxAPI(this, activity.getApplicationContext());
            permissionsManager = new PermissionsManager(this);
            callback = new MapActivityLocationCallback(view.getFragActivity());

        }

    }


    public void research(String localita, String termine, String categoria, String sottocategoria, float rating, String da, String a) {

        if (localita != null)
            dao.research(buildUrlString(localita, termine, categoria, sottocategoria, rating, da, a));
        else
            Toast.makeText(view.getContext(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();
    }

    public String buildUrlString(String localita, String termine, String categoria, String sottocategoria, float rating, String da, String a) {

        String urlString = "";

        if (termine.length() > 0) urlString += "nome=" + termine;
        else urlString += "nome=undef";

        urlString += "&categoria=" + categoria;

        if (localita.length() > 0) urlString += "&indirizzo=" + localita;
        else urlString += "&indirizzo=undef";

        urlString += "&sottocategoria=" + sottocategoria;
        urlString += "&rating=" + rating;

        if (da.length() > 0) urlString += "&prezzoda=" + da;
        else urlString += "&prezzoda=0.0";

        if (a.length() > 0) urlString += "&prezzoa=" + a;
        else urlString += "&prezzoa=0.0";

        return urlString;
    }

    public void switchStructureListToView(String[] nome, float[] rating, String[] indirizzo, String[] informazioni, int[] id, String[] copertinaUrl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine) {

        view.shareStructureArrayListWithListaStruttura(nome, rating, indirizzo, informazioni, id, copertinaUrl, prezzoda, prezzoa, longitudine, latitudine);
    }

    @Override
    public void getPlaceName(JSONObject response, Point coords) {

        JSONArray p = null;

        try {

            p = response.getJSONArray("features");
            JSONObject result = p.getJSONObject(0);
            view.setLocalita(result.getString("place_name"), coords);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyFrag() {
        locationEngine.removeLocationUpdates(callback);
    }

    public Context getContext() {
        return view.getContext();
    }


    @Override
    public void getLocationSuggestions(String searchText) {

        if (searchText.length() > 2) mapbox.getLocations(searchText);
        else {

            adapter.clear();
            locations.clear();
            view.hideSuggestionsList();
        }
    }

    @Override
    public void setAdapter(ListView listView) {
        listView.setAdapter(adapter);
    }

    private void initLocationEngine() {

        locationEngine = LocationEngineProvider.getBestLocationEngine(view.getContext());
        LocationEngineRequest request = new LocationEngineRequest.Builder(mapbox.getInterval())
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(mapbox.getWaitTime()).build();
        if (ActivityCompat.checkSelfPermission(view.getFragActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getFragActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationEngine.requestLocationUpdates(request, callback, view.getFragActivity().getMainLooper());
        if (ActivityCompat.checkSelfPermission(view.getFragActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getFragActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void enableGPS() {

        if(PermissionsManager.areLocationPermissionsGranted(view.getContext())){
            if(!checkGPSEnabled()) {
                turnOnGPS();
            }
            else {
                initLocationEngine();
                view.showRangeOption();
            }
        }
        else{
            permissionsManager.requestLocationPermissions(view.getFragActivity());
        }
    }

    @Override
    public void createLocationSuggestions(JSONObject response, String term) {

        JSONArray p=null;
        adapter.clear();
        locations.clear();

        try {
            p = response.getJSONArray("features");

            for(int i=0;i<p.length();i++) {

                JSONObject t;
                JSONArray h;
                t = p.getJSONObject(i);
                adapter.add(t.getString("place_name"));
                h=t.getJSONArray("center");
                locations.add(new LatLng(h.getDouble(1),h.getDouble(0)));
            }
            setListViewHeight();
            view.showSuggestionsList();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListViewHeight() {

        int totalHeight=0;
        ListView listView=view.getListView();
        for (int size=0; size < adapter.getCount(); size++) {

            View listItem=adapter.getView(size, null, listView);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height=totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    @Override
    public LatLng getLocation(int position) {
        return locations.get(position);
    }

    @Override
    public void showError() {

        Toast.makeText(view.getContext(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean checkGPSEnabled(){

        LocationManager locationManager = (LocationManager) view.getFragActivity().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void turnOnGPS(){

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(view.getFragActivity());
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {

                })
                .addOnFailureListener(e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(view.getFragActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e("GPS","Unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("GPS","Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                    }
                })
                .addOnCanceledListener(() -> Log.e("GPS","checkLocationSettings -> onCanceled"));
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) turnOnGPS();
    }

    @Override
    public void checkGPSChoiceResult(int requestCode, int resultCode) {

        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            initLocationEngine();
            view.showRangeOption();
        }
    }

    private class MapActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<Activity> activityWeakReference;

        MapActivityLocationCallback(Activity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {

            Activity activity = activityWeakReference.get();
            if (activity != null && checkGPSEnabled()) {
                Location location = result.getLastLocation();
                if (location == null) return;
                mapbox.getUserPlaceName(Point.fromLngLat(location.getLongitude(),location.getLatitude()));
                locationEngine.removeLocationUpdates(this);
            }
            else if(activity!=null) {
                locationEngine.removeLocationUpdates(this);
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
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}