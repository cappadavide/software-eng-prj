package com.example.consigliaviaggi.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.consigliaviaggi.PresenterToViewRicercaMappa;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.SearchPresenter;
import com.example.consigliaviaggi.presenter.RicercaMappaPresenter;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class RicercaMappa extends Fragment implements PresenterToViewRicercaMappa {

    private EditText searchLocation;
    private ListView listView;
    private SearchPresenter presenter;
    private Button button;
    private String MODE;

    public RicercaMappa (){}

    public RicercaMappa(String mode){
        this.MODE=mode;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(getArguments()!=null) this.MODE=getArguments().getString("mode");

        presenter=new RicercaMappaPresenter(this);
        View rootView = inflater.inflate(R.layout.ricerca_mappa, container, false);
        searchLocation= (EditText) rootView.findViewById(R.id.terminetext);

        searchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.getLocationSuggestions(s.toString());
            }
        });

        initListView(rootView);
        button=rootView.findViewById(R.id.usepositionbutton);
        button.setOnClickListener(v -> presenter.enableGPS());
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showSuggestionsList() {

        button.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSuggestionsList() {

        listView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.VISIBLE);
    }

    @Override
    public ListView getListView() {
        return listView;
    }

    @Override
    public void goOnMap() {

        Intent intent = new Intent(getActivity(), Map.class);
        Bundle b = new Bundle();
        b.putString("mode",MODE);
        b.putBoolean("position",true);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public Activity getFragActivity() {
        return getActivity();
    }

    private void initListView(View rootView){

        listView = rootView.findViewById(R.id.lv1);
        listView.setVisibility(View.INVISIBLE);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
                LatLng coords=presenter.getLocation(position);
                Intent intent = new Intent(getActivity(), Map.class);
                Bundle b = new Bundle();
                b.putString("mode",MODE);
                b.putBoolean("position",false);
                b.putString("place_name",listView.getItemAtPosition(position).toString());
                b.putDouble("lat",coords.getLatitude());
                b.putDouble("long",coords.getLongitude());
                intent.putExtras(b);
                startActivity(intent);
            }
            else
                Toast.makeText(getContext(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();

        });
        presenter.setAdapter(listView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        presenter.getPermissionsManager().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        presenter.checkGPSChoiceResult(requestCode,resultCode);
    }
}