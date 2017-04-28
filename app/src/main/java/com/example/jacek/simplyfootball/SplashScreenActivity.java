package com.example.jacek.simplyfootball;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Activity class that represents business model of a Splash Screen Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class SplashScreenActivity extends AppCompatActivity
{

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Call the Login Screen Activity after 3 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                // kill the current activity
                finish();
            }
        }, 3000);
    }
}
