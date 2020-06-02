package com.example.consigliaviaggi.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.bumptech.glide.Glide;
import com.example.consigliaviaggi.PresenterToViewMap;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.presenter.MapPresenter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map extends AppCompatActivity implements PresenterToViewMap {

    private MapView mapView;
    private MapboxMap mapBoxMap;
    private SearchView searchView;
    private MapboxDirections client;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private MapPresenter presenter;
    private Button filters;
    private boolean positionEnabled=false;
    private Button goBack;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Feature selectedFeature;
    private View bottomSheet;
    private FilterDialogView dialogView;
    private String categoria;
    private DirectionsRoute currentRoute;
    private GeoJsonSource source;
    private FeatureCollection featureCollection;
    private HashMap<String,String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
        getWindow().setExitTransition(new Explode());
        Mapbox.getInstance(this,getString(R.string.access_token));
        setContentView(R.layout.map_main);
        source=new GeoJsonSource("geojson-source");
        Bundle b = getIntent().getExtras();
        assert b != null;
        categoria=b.getString("mode");
        presenter=new MapPresenter(this,categoria);
        positionEnabled=b.getBoolean("position");

        if(!positionEnabled) initMapView(new LatLng(b.getDouble("lat"),b.getDouble("long")));
        else{
            initMapView(null);
            presenter.enableGPS();
        }

        mapView.onCreate(savedInstanceState);
        initSearchView();
        initListView();
        dialogView=new FilterDialogView(b.getString("mode"));
        floatingActionButton=findViewById(R.id.floatingActionButton2);
        goBack=findViewById(R.id.goBackMap);

        goBack.setOnClickListener(v -> {
            onBackPressed(); //Per tornare all'activity precedente
        });

        floatingActionButton.setOnClickListener(view -> {

            if(presenter.checkGPSEnabled()) showPosition();
            else presenter.enableGPS();
        });

        filters=findViewById(R.id.filter);
        filters.setOnClickListener(v -> dialogView.showDialog());
        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheet.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("titolo",selectedFeature.properties().get("title").getAsString());
            bundle.putString("informazioni",selectedFeature.properties().get("info").getAsString());
            bundle.putString("indirizzo",selectedFeature.properties().get("address").getAsString());
            bundle.putInt("id",selectedFeature.properties().get("id").getAsInt());
            bundle.putDouble("rating", selectedFeature.properties().get("rating").getAsDouble());

            try {
                JSONArray coords=new JSONObject(selectedFeature.geometry().toJson()).getJSONArray("coordinates");
                bundle.putDouble("latitudine",coords.getDouble(1));
                bundle.putDouble("longitudine",coords.getDouble(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bundle.putDouble("prezzoda",selectedFeature.properties().get("pricefrom").getAsDouble());
            bundle.putDouble("prezzoa",selectedFeature.properties().get("priceto").getAsDouble());
            String dist=((TextView)bottomSheet.findViewById(R.id.distanceText)).getText().toString();

            if(dist.length() > 0) bundle.putString("distanza",dist);
            openFacilitySheet(bundle);
        });

        searchView.clearFocus();
        searchView.setQuery(b.getString("place_name"),false);

        if(!positionEnabled){

            params=new HashMap<>();
            params.put("indirizzo",b.getString("place_name"));
            params.put("categoria",categoria);
            presenter.getFacilities(params,null);
            params=null;
        }

    }

    private void openFacilitySheet(Bundle b){

        startActivity(new Intent(getApplicationContext(), SchedaStruttura.class).putExtras(b), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

        dialogView.dialog.dismiss();
        super.onPause();
        mapView.onPause();
        presenter.onPauseMap();
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
        presenter.onDestroyMap();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void refreshSource() {

        if (source != null && featureCollection != null) source.setGeoJson(featureCollection);
    }

    private void initSearchView(){

        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                presenter.getLocationSuggestions(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                if(searchView.hasFocus()) presenter.getLocationSuggestions(newText);

                return false;
            }
        });
    }
    private void initListView(){

        listView = (ListView) findViewById(R.id.lv1);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            listView.setHeaderDividersEnabled(false);
            LatLng coords=presenter.getLocation(position);
            params=new HashMap<>();
            params.put("indirizzo",listView.getItemAtPosition(position).toString());
            params.put("categoria",categoria);
            presenter.getFacilities(params,coords);
            searchView.clearFocus();
            searchView.setQuery(listView.getItemAtPosition(position).toString(),false);
            presenter.clearResults();

        });

        presenter.setAdapter(listView);
    }

    private void initMapView(LatLng position){

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(mb -> {
            mapBoxMap=mb;
            mapBoxMap.setStyle(new Style.Builder().fromUri(getString(R.string.cv_style)), style -> {

                loadImages();
                mapBoxMap.getUiSettings().setCompassMargins(0,150,0,0);
                LocationComponent locationComponent = mapBoxMap.getLocationComponent();
                locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getApplicationContext(), Objects.requireNonNull(mapBoxMap.getStyle())).build());
                mapBoxMap.addOnMapClickListener(point -> handleClickIcon(mapBoxMap.getProjection().toScreenLocation(point)));
                style.addSource(source);
                SymbolLayer myLayer = new SymbolLayer("notSelectedLayer", "geojson-source");

                Objects.requireNonNull(mapBoxMap.getStyle()).addLayer(
                        myLayer.withProperties(
                                PropertyFactory.iconImage("cvb{poi}-15"),PropertyFactory.iconAllowOverlap(true),PropertyFactory.iconSize(1.2f)
                        )
                );

                SymbolLayer myLayer2 = new SymbolLayer("selectedLayer", "geojson-source");

                mapBoxMap.getStyle().addLayer(
                        myLayer2.withProperties(
                                PropertyFactory.iconImage("cvo{poi}-15"),PropertyFactory.iconSize(1.2f)

                        ).withFilter(Expression.eq(Expression.get("selected"),Expression.literal(true)))
                );

                if(position!=null){
                    CameraPosition newPlace = new CameraPosition.Builder()
                            .target(position)
                            .zoom(12)
                            .tilt(20)
                            .build();
                    mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPlace), 30);
                }
                else showPosition();
            });
        });
    }

    private boolean handleClickIcon(PointF toScreenLocation) {

        List<Feature> features = mapBoxMap.queryRenderedFeatures(toScreenLocation, "notSelectedLayer","selectedLayer");
        if (!features.isEmpty()) {

            selectedFeature = features.get(0);
            String title = selectedFeature.getStringProperty("title");
            assert featureCollection.features() != null;
            for (Feature feature : featureCollection.features()) {
                if (feature.getStringProperty("title").equals(title)) {
                    if(selectedFeature.getBooleanProperty("selected")) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        assert feature.properties() != null;
                        feature.properties().addProperty("selected", false);
                    }
                    else {
                        assert feature.properties() != null;
                        feature.properties().addProperty("selected", true);
                        setBottomSheet(feature);
                    }
                }
		else if(feature.getBooleanProperty("selected")){
			assert feature.properties() != null;
			feature.properties().addProperty("selected",false);
		}
            }

            refreshSource();
            return true;
        }
        else{
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            assert featureCollection.features() != null;
            for(Feature feature : featureCollection.features()){

                if(feature.getBooleanProperty("selected"))
                    feature.addBooleanProperty("selected",false);
            }
            refreshSource();
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void setBottomSheet(Feature feature) {

        TextView title=bottomSheet.findViewById(R.id.strutturaTitle);
        TextView price=bottomSheet.findViewById(R.id.priceText);
        title.setText(feature.getStringProperty("title"));
        price.setText(feature.properties().get("pricefrom").getAsString()+"€ - "+feature.properties().get("priceto").getAsString()+"€");
        ImageView preview=bottomSheet.findViewById(R.id.strutturaPreview);
        RatingBar ratingBar=bottomSheet.findViewById(R.id.ratingBar);
        ratingBar.setRating(feature.properties().get("rating").getAsFloat());
        preview.setClipToOutline(true);
        Glide.with(getBaseContext())
                .load(feature.getProperty("images").getAsJsonArray().get(0).getAsString())
                .centerCrop()
                .into(preview);
        if(positionEnabled) getDistance(feature);
        else mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void getDistance(Feature feature) {

        TextView distance=bottomSheet.findViewById(R.id.distanceText);
        Location lastKnownLocation=mapBoxMap.getLocationComponent().getLastKnownLocation();
        assert lastKnownLocation != null;
        Point userPosition= Point.fromLngLat(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude());
        Point destination=null;
        JSONArray coordsArray = null;

        try {
            assert feature.geometry() != null;
            coordsArray = new JSONObject(feature.geometry().toJson()).getJSONArray("coordinates");
            destination= Point.fromLngLat(coordsArray.getDouble(0),coordsArray.getDouble(1));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        client = MapboxDirections.builder()
                .origin(userPosition)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                if (response.body() == null) return;
                else if (response.body().routes().size() < 1) return;

                // Get the directions route
                currentRoute = response.body().routes().get(0);
                Float kilometers=new Float(currentRoute.distance()/1000);
                distance.setText(kilometers.toString()+"km");
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
                Toast.makeText(getApplicationContext(),"Controllare lo stato della connessione",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadImages(){

        Style style=mapBoxMap.getStyle();
        HashMap<String, Bitmap> images = new HashMap<>();

        if(categoria.equals("alberghi")){

            images.put("cvbhotel-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbbuilding_15)));
            images.put("cvohotel-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvobuilding_15)));
            images.put("cvbhome-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbhome_15)));
            images.put("cvohome-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvohome_15)));
            images.put("cvblodging-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvblodging_15)));
            images.put("cvolodging-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvolodging_15)));
        }
        else if(categoria.equals("attrazioni")){

            images.put("cvbamusementpark-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbamusement_park_15)));
            images.put("cvoamusementpark-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvoamusement_park_15)));
            images.put("cvbmuseum-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbtown_hall_15)));
            images.put("cvomuseum-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvotown_hall_15)));
            images.put("cvbzoo-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbveterinary_15)));
            images.put("cvozoo-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvoveterinary_15)));
        }
        else{

            images.put("cvbfastfood-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbfast_food_15)));
            images.put("cvofastfood-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvofast_food_15)));
            images.put("cvbrestaurant-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbrestaurant_15)));
            images.put("cvorestaurant-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvorestaurant_15)));
            images.put("cvbrestaurant_pizza-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbrestaurant_pizza_15)));
            images.put("cvorestaurant_pizza-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvorestaurant_pizza_15)));
        }

        images.put("cvbmarker-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvbmarker_15)));
        images.put("cvomarker-15",BitmapUtils.getBitmapFromDrawable(getDrawable(R.drawable.ic_cvomarker_15)));

        assert style != null;
        style.addImagesAsync(images);
    }

    @Override
    public void showSuggestionsList() {
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public MapboxMap getMapboxMap() {
        return mapBoxMap;
    }

    /*@Override
    public void showPlaces(String response) {
        featureCollection= FeatureCollection.fromJson(response);
        source.setGeoJson(featureCollection);
    }*/

    @Override
    public void showPlaces(String response,LatLng coords) {
        if(coords!=null){
            CameraPosition newPlace = new CameraPosition.Builder()
                    .target(coords)
                    .zoom(12)
                    .tilt(20)
                    .build();
            mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPlace), 30);
        }
        featureCollection= FeatureCollection.fromJson(response);
        source.setGeoJson(featureCollection);
    }

    @Override
    public void hideSuggestionsList() {
        listView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEnableGPSToast() {
        Toast.makeText(getApplicationContext(), "Attiva il GPS per visualizzare la tua posizione.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void setPlaceName(String placeName) {
        searchView.setQuery(placeName,false);
    }

    @Override
    public void showPosition() {

        positionEnabled=true;
        LocationComponent locationComponent = mapBoxMap.getLocationComponent();
        // Activate with a built LocationComponentActivationOptions object
        locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, Objects.requireNonNull(mapBoxMap.getStyle())).build());
        // Enable to make component visible
        locationComponent.setLocationComponentEnabled(true);
        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);
        // Set the component's render mode
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    @Override
    public boolean isMapBoxNull() { return mapBoxMap==null; }

    @Override
    public void hidePosition() { mapBoxMap.getLocationComponent().setLocationComponentEnabled(false); }

    @Override
    public void updatePosition(Location lastLocation) {

        LocationComponent locationComponent=mapBoxMap.getLocationComponent();
        locationComponent.forceLocationUpdate(lastLocation);
    }

    @Override
    public Activity getMapActivity() { return this; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        presenter.checkGPSChoiceResult(requestCode,resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        presenter.getPermissionsManager().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private class FilterDialogView{

        private String MODE;
        private Dialog dialog;
        private Button cancelDialog;
        private Button applyFilters;
        private RadioButton hotel, bedBreakfast, apartment;
        private RadioButton pizza,restaurant,fastFood;
        private RadioButton museum,park,zoo;
        private RadioButton other;
        private EditText prezzoda,prezzoa;
        private RadioButton star1,star2,star3,star4,star5;
        private ArrayList<RadioButton> hotels, restaurants,attractions,rating;

        private TextWatcher priceListener=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!applyFilters.isEnabled())
                    applyFilters.setEnabled(true);
            }
        };

        private View.OnClickListener starListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RadioButton)v).isChecked()) {
                    applyFilters.setEnabled(true);
                    changeSelectedRating((RadioButton) v);
                }
            }
        };

        private void changeSelectedRating(RadioButton selected) {
            for(RadioButton r:rating){
                if(selected.getId()!=r.getId())
                    r.setChecked(false);
            }
        }

        private View.OnClickListener facilitiesListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RadioButton)v).isChecked()){
                    applyFilters.setEnabled(true);
                    changeSelected((RadioButton) v);
                }
            }
        };

        private FilterDialogView(String mode){

            this.MODE=mode;
            initDialog();
            initList();
            addListener();
        }

        private void initDialog(){

            dialog=new Dialog(Map.this,R.style.AppTheme);

            switch(MODE){

                case "alberghi":
                    hotels=new ArrayList<>();
                    dialog.setContentView(R.layout.filterdialog_alberghi);
                    hotel=dialog.findViewById(R.id.hotel_RadioDialog);
                    bedBreakfast=dialog.findViewById(R.id.bb_RadioDialog);
                    apartment=dialog.findViewById(R.id.appartamento_RadioDialog);
                    break;

                case "ristoranti":
                    restaurants=new ArrayList<>();
                    dialog.setContentView(R.layout.filterdialog_ristoranti);
                    pizza=dialog.findViewById(R.id.pizzeria_RadioDialog);
                    restaurant=dialog.findViewById(R.id.ristorante_RadioDialog);
                    fastFood=dialog.findViewById(R.id.fastfood_RadioDialog);
                    break;

                case "attrazioni":
                    attractions=new ArrayList<>();
                    dialog.setContentView(R.layout.filterdialog_attrazioni);
                    museum=dialog.findViewById(R.id.museo_RadioDialog);
                    park=dialog.findViewById(R.id.parcogiochi_RadioDialog);
                    zoo=dialog.findViewById(R.id.zoo_RadioDialog);
                    break;
            }

            rating=new ArrayList<>();
            star1=dialog.findViewById(R.id.star1RadioButton);
            star2=dialog.findViewById(R.id.star2RadioButton);
            star3=dialog.findViewById(R.id.star3RadioButton);
            star4=dialog.findViewById(R.id.star4RadioButton);
            star5=dialog.findViewById(R.id.star5RadioButton);
            other=dialog.findViewById(R.id.altro_RadioDialog);
            prezzoda=dialog.findViewById(R.id.daDialog);
            prezzoa=dialog.findViewById(R.id.aDialog);
            prezzoda.addTextChangedListener(priceListener);
            prezzoa.addTextChangedListener(priceListener);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.transpWhite2)));
            cancelDialog=dialog.findViewById(R.id.closeDialogButton);
            cancelDialog.setOnClickListener(v -> dialog.hide());
            applyFilters=dialog.findViewById(R.id.useFilters);

            applyFilters.setOnClickListener(v -> {
                params=new HashMap<>();
                params.put("categoria",MODE);
                getRating();
                getSubCategory();
                params.put("indirizzo",searchView.getQuery().toString());
                String prezzodaString=prezzoda.getText().toString();

                if(prezzodaString.length() > 0) params.put("prezzoda",prezzodaString);
                String prezzoaString=prezzoa.getText().toString();
                if(prezzoaString.length() > 0) params.put("prezzoa",prezzoaString);

                dialog.hide();
                presenter.getFacilities(params,null);
                disableRadioButtons();
                disableRating();
                applyFilters.setEnabled(false);
            });
        }

        private void disableRating() {

            for(RadioButton r:rating){
                r.setChecked(false);
            }
        }

        private void disableRadioButtons() {

            switch(MODE){

                case "alberghi":

                    for(RadioButton r:hotels){
                        r.setChecked(false);
                    }
                    break;

                case "ristoranti":

                    for(RadioButton r:restaurants){
                        r.setChecked(false);
                    }
                    break;

                case "attrazioni":

                    for(RadioButton r:attractions){
                        r.setChecked(false);
                    }
                    break;
            }
        }


        private void addListener(){

            switch(MODE){

                case "alberghi":
                    hotel.setOnClickListener(facilitiesListener);
                    bedBreakfast.setOnClickListener(facilitiesListener);
                    apartment.setOnClickListener(facilitiesListener);
                    break;

                case "ristoranti":
                    pizza.setOnClickListener(facilitiesListener);
                    restaurant.setOnClickListener(facilitiesListener);
                    fastFood.setOnClickListener(facilitiesListener);
                    break;

                case "attrazioni":
                    museum.setOnClickListener(facilitiesListener);
                    park.setOnClickListener(facilitiesListener);
                    zoo.setOnClickListener(facilitiesListener);
                    break;
            }

            star1.setOnClickListener(starListener);
            star2.setOnClickListener(starListener);
            star3.setOnClickListener(starListener);
            star4.setOnClickListener(starListener);
            star5.setOnClickListener(starListener);
            other.setOnClickListener(facilitiesListener);
        }

        private void initList(){


            switch(MODE){

                case "alberghi":
                    hotels.add(hotel);
                    hotels.add(bedBreakfast);
                    hotels.add(apartment);
                    hotels.add(other);
                    break;

                case "ristoranti":
                    restaurants.add(pizza);
                    restaurants.add(fastFood);
                    restaurants.add(restaurant);
                    restaurants.add(other);
                    break;

                case "attrazioni":
                    attractions.add(museum);
                    attractions.add(zoo);
                    attractions.add(park);
                    attractions.add(other);
                    break;
            }

            rating.add(star1);
            rating.add(star2);
            rating.add(star3);
            rating.add(star4);
            rating.add(star5);
        }
        private void changeSelected(RadioButton selected) {

            switch (MODE) {

                case "alberghi":

                    for(RadioButton r:hotels){
                        if(selected.getId()!=r.getId())
                            r.setChecked(false);
                    }
                    break;

                case "ristoranti":

                    for(RadioButton r:restaurants){
                        if(selected.getId()!=r.getId())
                            r.setChecked(false);
                    }
                    break;

                case "attrazioni":

                    for(RadioButton r:attractions){
                        if(selected.getId()!=r.getId())
                            r.setChecked(false);
                    }
                    break;
            }
        }

        private void getRating(){

            for(RadioButton r:rating){

                if(r.isChecked()){

                    switch (r.getId()){

                        case R.id.star1RadioButton:
                            params.put("rating","1");
                            break;

                        case R.id.star2RadioButton:
                            params.put("rating","2");
                            break;

                        case R.id.star3RadioButton:
                            params.put("rating","3");
                            break;

                        case R.id.star4RadioButton:
                            params.put("rating","4");
                            break;

                        case R.id.star5RadioButton:
                            params.put("rating","5");
                            break;
                    }
                }
            }
        }

        private void getSubCategory(){

            switch(MODE){

                case "alberghi":

                    for(RadioButton r: hotels){
                        if(r.isChecked()){
                            String subcategory=getResources().getResourceEntryName(r.getId());
                            params.put("sottocategoria",subcategory.substring(0,subcategory.indexOf('_')));
                        }
                    }
                    break;

                case "ristoranti":

                    for(RadioButton r: restaurants){
                        if(r.isChecked()){
                            String subcategory=getResources().getResourceEntryName(r.getId());
                            params.put("sottocategoria",subcategory.substring(0,subcategory.indexOf('_')));
                        }
                    }

                    break;

                case "attrazioni":

                    for(RadioButton r: attractions){
                        if(r.isChecked()){
                            String subcategory=getResources().getResourceEntryName(r.getId());
                            params.put("sottocategoria",subcategory.substring(0,subcategory.indexOf('_')));
                        }
                    }
                    break;
            }
        }

        public void showDialog() {dialog.show(); }
    }
}