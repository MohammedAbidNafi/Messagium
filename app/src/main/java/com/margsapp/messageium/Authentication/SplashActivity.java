package com.margsapp.messageium.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.margsapp.messageium.R;

import java.util.Locale;

import static com.margsapp.messageium.Settings.CustomiseActivity.THEME;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("lang_settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("lang","");
        setLocale(language);


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

        // Using handler with postDelayed called runnable run method
        new Handler().postDelayed(() -> {

            Intent i = new Intent(SplashActivity.this, StartActivity.class);

            startActivity(i);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            // close this activity
            finish();

        }, 3*1000); // wait for 2 seconds
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }
}