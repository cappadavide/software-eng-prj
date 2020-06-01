package com.example.consigliaviaggi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.consigliaviaggi.PresenterToViewListaStrutture;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.ToPresenterListaStrutture;
import com.example.consigliaviaggi.model.Struttura;
import com.example.consigliaviaggi.presenter.ListaStrutturePresenter;
import com.example.consigliaviaggi.presenter.utils.CardAdapter;
import com.mapbox.geojson.Point;
import java.util.ArrayList;
import java.util.Objects;

public class ListaStrutture extends AppCompatActivity implements PresenterToViewListaStrutture {

    private TextView numeroStruttureTrovate;
    private ToPresenterListaStrutture presenter;
    private RecyclerView recyclerView;
    private ArrayList<Struttura> strutturaArrayList = new ArrayList<>();
    private Toolbar toolbar;
    private boolean result;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_strutture);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Strutture trovate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        numeroStruttureTrovate = findViewById(R.id.numstrutture);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        presenter = new ListaStrutturePresenter(this);

        Bundle extras = getIntent().getExtras();

        if(extras!=null) {

            String[] nome = extras.getStringArray("nomi");
            String[] indirizzo = extras.getStringArray("indirizzo");
            String[] informazioni = extras.getStringArray("info");
            String[] copertinaUrl = extras.getStringArray("url");
            int[] strutturaId = extras.getIntArray("id");
            float[] rating = extras.getFloatArray("rating");
            double[] prezzoda = extras.getDoubleArray("prezzoda");
            double[] prezzoa = extras.getDoubleArray("prezzoa");
            double[] longitudine = extras.getDoubleArray("longitudine");
            double[] latitudine = extras.getDoubleArray("latitudine");
            int range=extras.getInt("range");
            Point coords=Point.fromLngLat(extras.getDouble("utenteLong"),extras.getDouble("utenteLat"));

            if(extras.containsKey("range")){
                result=presenter.createStructureList(nome,rating,indirizzo,informazioni,strutturaId,copertinaUrl,prezzoda, prezzoa, longitudine, latitudine, strutturaArrayList,range,coords);
            }
            else{
                result=presenter.createStructureList(nome,rating,indirizzo,informazioni,strutturaId,copertinaUrl,prezzoda, prezzoa, longitudine, latitudine, strutturaArrayList);
                showResults();
            }
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showResults() {

        if(result) {
            if(strutturaArrayList!=null && strutturaArrayList.size()==1)
                numeroStruttureTrovate.setText("La ricerca ha prodotto "+strutturaArrayList.size()+" risultato");

            else {
                if(strutturaArrayList != null) {
                    strutturaArrayList.size();
                    numeroStruttureTrovate.setText("La ricerca ha prodotto " + strutturaArrayList.size() + " risultati");
                }
            }
            recyclerView.setAdapter(new CardAdapter(this, strutturaArrayList,1));
        }
        else numeroStruttureTrovate.setText("La ricerca ha prodotto 0 risultati");
    }
}