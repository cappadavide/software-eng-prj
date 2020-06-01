package com.example.consigliaviaggi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.example.consigliaviaggi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationBottom = findViewById(R.id.navigation);
        navigationBottom.setSelectedItemId(R.id.homeicon);
        navigationBottom.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new HomeView()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener= menuItem -> {

        Fragment selectedFragment = null;

        switch (menuItem.getItemId()) {

            case R.id.homeicon:
                selectedFragment = new HomeView();
                break;

            case R.id.recensioni:
                selectedFragment = new RecensioniScritte();
                break;

            case R.id.impostazioni:
                selectedFragment = new Impostazioni();
                break;
        }
        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, selectedFragment).commit();
        return true;
    };
}