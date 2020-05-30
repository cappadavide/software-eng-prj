package com.example.consigliaviaggi.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.consigliaviaggi.PresenterToViewSchedaStruttura;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.ToPresenterSchedaStruttura;
import com.example.consigliaviaggi.model.Foto;
import com.example.consigliaviaggi.model.Recensione;
import com.example.consigliaviaggi.presenter.SchedaStrutturaPresenter;
import com.example.consigliaviaggi.presenter.utils.CardAdapter;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation;
import com.mapbox.geojson.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class SchedaStruttura extends AppCompatActivity implements PresenterToViewSchedaStruttura,View.OnClickListener {

    private RecyclerView recyclerViewFoto, recyclerViewRecensione;
    private Button loginButton, scriviRecensioneButton,posizioneStruttura;
    private RadioButton star1, star2, star3, star4, star5;
    private TextView informazioni, lasciaRecensione, effettuaLogin, indirizzo, prezzo, distanza;
    private Dialog dialog;
    private MapboxStaticMap map;
    private RatingBar ratingBar;
    private ImageView icongps;
    private ToPresenterSchedaStruttura presenter;
    private int id, rating=0;
    private Toolbar toolbar;
    private double longitudine, latitudine;
    private int onresume;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedastruttura);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());
        icongps=findViewById(R.id.icondistance);
        star1 = (RadioButton)findViewById(R.id.radior1);
        star2 = (RadioButton)findViewById(R.id.radior2);
        star3 = (RadioButton)findViewById(R.id.radior3);
        star4 = (RadioButton)findViewById(R.id.radior4);
        star5 = (RadioButton)findViewById(R.id.radior5);
        loginButton =findViewById(R.id.loginbtn);
        scriviRecensioneButton =findViewById(R.id.recebtn);
        informazioni =findViewById(R.id.info);
        ratingBar=findViewById(R.id.ratingrow);
        lasciaRecensione =findViewById(R.id.recetxt);
        effettuaLogin =findViewById(R.id.logintxt);
        distanza=findViewById(R.id.distanzaScheda);
        indirizzo=findViewById(R.id.ind);
        prezzo=findViewById(R.id.prezzo);
        posizioneStruttura=findViewById(R.id.showPositionButton);

        Bundle bundle = this.getIntent().getExtras();

        if(bundle!=null) {

            getSupportActionBar().setTitle(bundle.getString("titolo"));
            longitudine=bundle.getDouble("longitudine");
            latitudine=bundle.getDouble("latitudine");
            id=bundle.getInt("id");
            indirizzo.setText(bundle.getString("indirizzo"));
            informazioni.setText(bundle.getString("informazioni"));
            ratingBar.setRating((float) bundle.getDouble("rating"));
            String dist=bundle.getString("distanza");

            if(dist!=null){
                distanza.setText("A "+dist+" da te");
                icongps.setVisibility(VISIBLE);
                distanza.setVisibility(VISIBLE);
            }
            prezzo.setText(bundle.getDouble("prezzoda")+"-"+bundle.getDouble("prezzoa")+"€");
        }

        star1.setOnClickListener(v -> {
            rating= 1;
            presenter.obtainReviewsById(id,rating);
            star2.setChecked(false);
            star3.setChecked(false);
            star4.setChecked(false);
            star5.setChecked(false);

        });

        star2.setOnClickListener(v -> {
            rating= 2;
            presenter.obtainReviewsById(id,rating);
            star1.setChecked(false);
            star3.setChecked(false);
            star4.setChecked(false);
            star5.setChecked(false);

        });

        star3.setOnClickListener(v -> {
            rating= 3;
            presenter.obtainReviewsById(id,rating);
            star2.setChecked(false);
            star1.setChecked(false);
            star4.setChecked(false);
            star5.setChecked(false);

        });

        star4.setOnClickListener(v -> {
            rating= 4;
            presenter.obtainReviewsById(id,rating);
            star2.setChecked(false);
            star3.setChecked(false);
            star1.setChecked(false);
            star5.setChecked(false);

        });

        star5.setOnClickListener(v -> {
            rating= 5;
            presenter.obtainReviewsById(id,rating);
            star2.setChecked(false);
            star3.setChecked(false);
            star4.setChecked(false);
            star1.setChecked(false);

        });

        List<StaticMarkerAnnotation> markers = new ArrayList<>();
        markers.add(StaticMarkerAnnotation.builder().name(StaticMapCriteria.SMALL_PIN)
                        .color(255,0,0)
                        .lnglat(Point.fromLngLat(longitudine, latitudine)).build());
        dialog=new Dialog(this,R.style.AppTheme);
        map= MapboxStaticMap.builder()
                .accessToken(getString(R.string.access_token))
                .styleId(StaticMapCriteria.STREET_STYLE)
                .staticMarkerAnnotations(markers)
                .cameraPoint(Point.fromLngLat(longitudine,latitudine)) // Image's centerpoint on map
                .cameraZoom(13)
                .width(320) // Image width
                .height(320) // Image height
                .retina(true) // Retina 2x image will be returned
                .build();

        posizioneStruttura.setOnClickListener(v -> {
            dialog.setContentView(R.layout.posizionemappa_dialog);
            ImageView image=dialog.findViewById(R.id.mappaStatica);
            Glide.with(getBaseContext())
                    .load(map.url().toString())
                    .centerCrop()
                    .into(image);
            dialog.show();

        });
        recyclerViewFoto = findViewById(R.id.recyclefoto);
        recyclerViewRecensione = findViewById(R.id.recyclerece);

        recyclerViewFoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecensione.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        loginButton.setOnClickListener(this);
        scriviRecensioneButton.setOnClickListener(this);

        presenter = new SchedaStrutturaPresenter(this);
        presenter.obtainReviewsById(id,rating);
        presenter.obtainPhotosById(id);

        if(presenter.enableButtonBySharedPreferences(this).equals("login")) {

            onresume=1;
            loginButton.setVisibility(INVISIBLE);
            lasciaRecensione.setVisibility(VISIBLE);
            scriviRecensioneButton.setVisibility(VISIBLE);
        }
        else{

            onresume=0;
            scriviRecensioneButton.setVisibility(INVISIBLE);
            lasciaRecensione.setVisibility(INVISIBLE);
            effettuaLogin.setVisibility(VISIBLE);
            loginButton.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.loginbtn:

                presenter.setSharedPreferences(1, getApplicationContext(), id);
                Intent reg = new Intent(this, Login.class);
                startActivity(reg);
                break;

            case R.id.recebtn:

                presenter.setSharedPreferences(2, getApplicationContext(), id);
                Intent reg2 = new Intent(this, ScriveRecensione.class);
                startActivity(reg2);
                break;
        }
    }

    public void setReviewsList(ArrayList<Recensione> arrayList){

        if(arrayList!=null && !arrayList.isEmpty()){

            recyclerViewRecensione.setVisibility(INVISIBLE);
            recyclerViewRecensione.setAdapter(new CardAdapter(this, arrayList,3));
            recyclerViewRecensione.setVisibility(VISIBLE);
        }
    }

    public void setPhotosList(ArrayList<Foto> arrayList){

        if(arrayList!=null && !arrayList.isEmpty()){
            recyclerViewFoto.setAdapter(new CardAdapter(this, arrayList,2));
        }
    }

    public Context getContext(){ return getApplicationContext();}

    @Override
    public void onRestart() {

        super.onRestart();
        if(presenter.enableButtonBySharedPreferences(this).equals("login")) onresume=1;
        else onresume=0;

        if (onresume == 1) {

            effettuaLogin.setVisibility(INVISIBLE);
            loginButton.setVisibility(INVISIBLE);
            lasciaRecensione.setVisibility(VISIBLE);
            scriviRecensioneButton.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if(dialog!=null) dialog.dismiss();
    }
}
