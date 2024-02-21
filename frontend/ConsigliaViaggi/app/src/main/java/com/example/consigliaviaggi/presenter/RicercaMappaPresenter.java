package com.example.consigliaviaggi.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.example.consigliaviaggi.PresenterToMapboxAPI;
import com.example.consigliaviaggi.PresenterToStrutturaDAO;
import com.example.consigliaviaggi.PresenterToViewRicercaMappa;
import com.example.consigliaviaggi.SearchPresenter;
import com.example.consigliaviaggi.dao.MapboxAPI;
import com.example.consigliaviaggi.model.Struttura;
import com.example.consigliaviaggi.presenter.utils.ListAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.geometry.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class RicercaMappaPresenter implements SearchPresenter, PermissionsListener {

    private PresenterToViewRicercaMappa view;
    private PresenterToStrutturaDAO dao;
    private Struttura struttura;
    private ListAdapter adapter;
    private PermissionsManager permissionsManager;
    private ArrayList<LatLng> locations = new ArrayList<>();
    private PresenterToMapboxAPI mapbox;
    private static final int REQUEST_CHECK_SETTINGS = 214;


    public RicercaMappaPresenter(PresenterToViewRicercaMappa view){
        this.view=view;
        struttura = new Struttura();
        this.view=view;
        adapter=new ListAdapter(view.getFragActivity(), new ArrayList<>());
        mapbox=new MapboxAPI(this,view.getFragActivity());
        permissionsManager=new PermissionsManager(this);
    }

    @Override
    public void getLocationSuggestions(String searchText) {

        if(searchText.length()>2){
            mapbox.getLocations(searchText);
        }
        else{
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
    public void enableGPS() {

        if(PermissionsManager.areLocationPermissionsGranted(view.getFragActivity())){
            if(!checkGPSEnabled()) turnOnGPS();
            else{
                if(isNetworkConnected())
                    view.goOnMap();
                else
                    showError();
            }
        }
        else permissionsManager.requestLocationPermissions(view.getFragActivity());
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

        Toast.makeText(view.getFragActivity(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean checkGPSEnabled(){

        LocationManager locationManager = (LocationManager) view.getFragActivity().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void checkGPSChoiceResult(int requestCode, int resultCode) {

        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK){
            if(isNetworkConnected())
                view.goOnMap();
            else
                showError();
        }
    }

    private void turnOnGPS(){

        final Activity activity=view.getFragActivity();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(activity);
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
                                rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) view.getFragActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}