package com.example.consigliaviaggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.consigliaviaggi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationBottom = findViewById(R.id.navigation);
        navigationBottom.setOnNavigationItemSelectedListener(navListener);
        //qui si deve far partire il primo fragment
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {

                case R.id.homeicon:
                    //
                    break;

                case R.id.recensioni:
                    //
                    break;

                case R.id.impostazioni:
                    //
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, selectedFragment).commit();
            return true;
        }
    };
}
