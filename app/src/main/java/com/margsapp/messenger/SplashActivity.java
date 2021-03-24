package com.margsapp.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.margsapp.messenger.CustomiseActivity.THEME;
import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("theme", 0);
        String Theme = preferences.getString(THEME, "");
        if(Theme.equals("2")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(Theme.equals("1")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(Theme.equals("0")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_splash);




        new Handler().postDelayed(new Runnable() {

        // Using handler with postDelayed called runnable run method

            @Override

            public void run() {

                Intent i = new Intent(SplashActivity.this, StartActivity.class);

                startActivity(i);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                // close this activity
                finish();

            }

        }, 2*1000); // wait for 5 seconds
    }

}