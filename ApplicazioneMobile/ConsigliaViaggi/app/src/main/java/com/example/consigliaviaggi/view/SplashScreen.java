package com.example.consigliaviaggi.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.consigliaviaggi.R;

public class SplashScreen extends Activity {

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(R.layout.activity_splashscreen);

        /** Duration of wait **/
        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(() -> {

            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
            this.startActivity(mainIntent);
            this.finish();

        }, SPLASH_DISPLAY_LENGTH);
    }
}