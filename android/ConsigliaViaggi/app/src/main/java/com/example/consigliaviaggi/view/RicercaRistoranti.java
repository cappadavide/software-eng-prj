package com.example.consigliaviaggi.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.consigliaviaggi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RicercaRistoranti extends AppCompatActivity {

    private Toolbar toolbar;
    Fragment selectedFragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_ristoranti);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ricerca ristoranti");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BottomNavigationView b=findViewById(R.id.search);

        b.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.framericerca, selectedFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            selectedFragment = null;

            switch (menuItem.getItemId()) {

                case R.id.cerca:
                    //
                    break;

                case R.id.mappa:
                    //
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.framericerca, selectedFragment).commit();
            return true;
        }
    };


}

