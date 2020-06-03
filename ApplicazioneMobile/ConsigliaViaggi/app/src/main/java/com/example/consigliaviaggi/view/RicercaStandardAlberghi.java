package com.example.consigliaviaggi.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.consigliaviaggi.PresenterToViewRicercaStruttura;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.SearchPresenter;
import com.example.consigliaviaggi.presenter.RicercaPresenter;
import com.mapbox.geojson.Point;
import java.util.Objects;

public class RicercaStandardAlberghi extends Fragment implements PresenterToViewRicercaStruttura {

    private RadioButton hotel, appartamento, altro, BandB, star1, star2, star3, star4, star5;
    private SearchPresenter.ToPresenterRicercaStruttura presenter;
    private Button gpsButton,cercaButton;
    private SeekBar seekBar;
    private TextView rangeTitle,range,localitaTitle;
    private boolean gpsEnabled=false;
    private String sottocategoria ="undef";
    private String luogoUtente;
    private Point userCoords;
    private float rating=0;
    private EditText nome, localita, prezzoda, prezzoa;
    private ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_ricerca_standard_alberghi, container, false);
        rangeTitle=rootView.findViewById(R.id.distanzatitle);
        range=rootView.findViewById(R.id.distanzavalue);
        gpsButton=rootView.findViewById(R.id.geoloc);
        seekBar=rootView.findViewById(R.id.seekBar);
        nome = rootView.findViewById(R.id.terminetext);
        localita = rootView.findViewById(R.id.localitatext);
        localitaTitle=rootView.findViewById(R.id.localita);
        prezzoda = rootView.findViewById(R.id.da);
        prezzoa = rootView.findViewById(R.id.a);
        hotel = (RadioButton)rootView.findViewById(R.id.radiohotel);
        BandB = (RadioButton)rootView.findViewById(R.id.radiobb);
        appartamento = (RadioButton)rootView.findViewById(R.id.radioapp);
        altro = (RadioButton)rootView.findViewById(R.id.radioaltro);

        star1 = (RadioButton)rootView.findViewById(R.id.radior1);
        star2 = (RadioButton)rootView.findViewById(R.id.radior2);
        star3 = (RadioButton)rootView.findViewById(R.id.radior3);
        star4 = (RadioButton)rootView.findViewById(R.id.radior4);
        star5 = (RadioButton)rootView.findViewById(R.id.radior5);

        presenter = new RicercaPresenter(this, Objects.requireNonNull(getActivity()));
        initListView(rootView);
        gpsButton.setOnClickListener(v -> {

            if(gpsEnabled){

                rangeTitle.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.INVISIBLE);
                range.setVisibility(View.INVISIBLE);
                localita.setVisibility(View.VISIBLE);
                localitaTitle.setVisibility(View.VISIBLE);
                gpsButton.setBackground(getResources().getDrawable(R.drawable.geoloc,null));
                gpsEnabled=false;

            }
            else presenter.enableGPS();
        });
        star1.setOnClickListener(v -> {

            star2.setChecked(false);
            star3.setChecked(false);
            star4.setChecked(false);
            star5.setChecked(false);
            rating= (float) 1.0;
            cercaButton.setEnabled(true);
        });

        star2.setOnClickListener(v -> {

            star1.setChecked(false);
            star3.setChecked(false);
            star4.setChecked(false);
            star5.setChecked(false);
            rating= (float) 2.0;
            cercaButton.setEnabled(true);
        });

        star3.setOnClickListener(v -> {

            star2.setChecked(false);
            star1.setChecked(false);
            star4.setChecked(false);
            star5.setChecked(false);
            rating= (float) 3.0;
            cercaButton.setEnabled(true);
        });

        star4.setOnClickListener(v -> {

            star2.setChecked(false);
            star3.setChecked(false);
            star1.setChecked(false);
            star5.setChecked(false);
            rating= (float) 4.0;
            cercaButton.setEnabled(true);
        });

        star5.setOnClickListener(v -> {

            star2.setChecked(false);
            star3.setChecked(false);
            star4.setChecked(false);
            star1.setChecked(false);
            rating= (float) 5.0;
            cercaButton.setEnabled(true);
        });

        hotel.setOnClickListener(v -> {

            BandB.setChecked(false);
            appartamento.setChecked(false);
            altro.setChecked(false);
            sottocategoria ="hotel";
            cercaButton.setEnabled(true);
        });

        appartamento.setOnClickListener(v -> {

            BandB.setChecked(false);
            hotel.setChecked(false);
            altro.setChecked(false);
            sottocategoria ="appartamento";
            cercaButton.setEnabled(true);
        });

        altro.setOnClickListener(v -> {

            BandB.setChecked(false);
            hotel.setChecked(false);
            appartamento.setChecked(false);
            sottocategoria ="altro";
            cercaButton.setEnabled(true);
        });

        BandB.setOnClickListener(v -> {

            appartamento.setChecked(false);
            hotel.setChecked(false);
            altro.setChecked(false);
            sottocategoria ="bb";
            cercaButton.setEnabled(true);
        });

        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.blue), PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                range.setText(progress*50+"m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cercaButton = rootView.findViewById(R.id.searchbutton);
        cercaButton.setOnClickListener(view -> {

            if(gpsEnabled)
                presenter.research(luogoUtente,nome.getText().toString().trim(),
                        "alberghi",sottocategoria,rating, prezzoda.getText().toString(), prezzoa.getText().toString());
            else
                presenter.research(localita.getText().toString().trim(),nome.getText().toString().trim(),
                        "alberghi",sottocategoria,rating, prezzoda.getText().toString(), prezzoa.getText().toString());
        });

        nome.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String termineInput = nome.getText().toString();
                cercaButton.setEnabled(termineInput.length()>0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        }
        );

        localita.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String localitaInput = localita.getText().toString();
                cercaButton.setEnabled(localitaInput.length()>0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(localita.hasFocus())
                    presenter.getLocationSuggestions(s.toString());
            }
        }
        );
        prezzoda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String prezzoDaInput = prezzoda.getText().toString();
                cercaButton.setEnabled(prezzoDaInput.length()>0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        prezzoa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String prezzoAInput = prezzoa.getText().toString();
                cercaButton.setEnabled(prezzoAInput.length()>0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); }

    public void shareStructureArrayListWithListaStruttura(String[] nome, float[] rating, String[] indirizzo, String[] informazioni, int[] id, String[] copertinaUrl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine) {

        Intent intent = new Intent(getActivity(), ListaStrutture.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("nomi", nome);
        bundle.putFloatArray("rating", rating);
        bundle.putStringArray("indirizzo", indirizzo);
        bundle.putStringArray("info", informazioni);
        bundle.putIntArray("id", id);
        bundle.putStringArray("url", copertinaUrl);
        bundle.putDoubleArray("prezzoda", prezzoda);
        bundle.putDoubleArray("prezzoa", prezzoa);
        bundle.putDoubleArray("longitudine", longitudine);
        bundle.putDoubleArray("latitudine", latitudine);

        if (gpsEnabled) {

            String rangeString = range.getText().toString();
            bundle.putInt("range", Integer.parseInt(rangeString.substring(0, rangeString.length() - 1)));
            bundle.putDouble("utenteLong", userCoords.longitude());
            bundle.putDouble("utenteLat", userCoords.latitude());
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showRangeOption() {

        localita.setVisibility(View.INVISIBLE);
        localitaTitle.setVisibility(View.INVISIBLE);
        gpsButton.setBackground(getResources().getDrawable(R.drawable.geoloc_on,null));
        gpsEnabled=true;
        rangeTitle.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        range.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLocalita(String placename, Point coords) {

        luogoUtente=placename;
        userCoords=coords;
        cercaButton.setEnabled(true);
    }

    @Override
    public void showSuggestionsList() {
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSuggestionsList() {
        listView.setVisibility(View.INVISIBLE);

    }

    @Override
    public ListView getListView() {
        return listView;
    }

    public Context getContext(){ return getActivity(); }

    @Override
    public Activity getFragActivity() {
        return getActivity();
    }

    private void initListView(View rootView){

        listView = rootView.findViewById(R.id.lv1);
        listView.setVisibility(View.INVISIBLE);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            localita.clearFocus();
            localita.setText(listView.getItemAtPosition(position).toString());
            listView.setVisibility(View.INVISIBLE);
        });
        presenter.setAdapter(listView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        presenter.checkGPSChoiceResult(requestCode,resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        presenter.getPermissionsManager().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}