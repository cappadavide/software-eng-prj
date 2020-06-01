package com.example.consigliaviaggi.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import com.example.consigliaviaggi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Objects;

public class RicercaAlberghi extends AppCompatActivity{

    private Toolbar toolbar;
    Fragment selectedFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_alberghi);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Ricerca alberghi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BottomNavigationView b=findViewById(R.id.search);

        b.setOnNavigationItemSelectedListener(navListener);
        selectedFragment=new RicercaStandardAlberghi();
        getSupportFragmentManager().beginTransaction().replace(R.id.framericerca, selectedFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener= menuItem -> {

        switch (menuItem.getItemId()) {

            case R.id.cerca:
                selectedFragment = new RicercaStandardAlberghi();
                break;

            case R.id.mappa:
                selectedFragment = new RicercaMappa("alberghi");
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.framericerca, selectedFragment).commit();
        return true;
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        selectedFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        selectedFragment.onActivityResult(requestCode,resultCode,data);
    }
}