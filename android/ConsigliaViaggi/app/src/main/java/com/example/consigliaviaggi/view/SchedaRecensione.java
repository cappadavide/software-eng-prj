package com.example.consigliaviaggi.view;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.consigliaviaggi.R;
import java.util.Objects;

public class SchedaRecensione extends AppCompatActivity {

    private TextView struttura, corpo;
    private RatingBar rating;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedarecensione);

        toolbar = findViewById(R.id.toolbarrow);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Recensione completa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        struttura = (TextView)findViewById(R.id.strutturaname);
        corpo = (TextView)findViewById(R.id.bodyrece);
        rating = (RatingBar)findViewById(R.id.ratingg);

        Bundle bundle = this.getIntent().getExtras();
        assert bundle != null;
        struttura.setText(bundle.getString("struttura"));
        corpo.setText(bundle.getString("bodyrow"));
        rating.setRating(bundle.getInt("rating"));

        toolbar.setNavigationOnClickListener(v -> finish());
    }
}