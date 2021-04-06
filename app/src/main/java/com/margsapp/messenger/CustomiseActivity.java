package com.margsapp.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Objects;

public class CustomiseActivity extends AppCompatActivity {

    public static final String THEME = "0";
    MaterialButtonToggleGroup materialButtonToggleGroup;

    ImageView sun,default_settings,moon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        sun = findViewById(R.id.sun_img);
        moon = findViewById(R.id.moon_img);
        default_settings = findViewById(R.id.default_img);
        sun.setVisibility(View.GONE);
        moon.setVisibility(View.GONE);
        default_settings.setVisibility(View.VISIBLE);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Customise");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        materialButtonToggleGroup = findViewById(R.id.btg_theme);
        materialButtonToggleGroup.check(R.id.btnDefault);

        loadData();
        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                    if(checkedId == R.id.btnLight){

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        moon.setVisibility(View.GONE);
                        sun.setVisibility(View.VISIBLE);
                        default_settings.setVisibility(View.GONE);
                        SharedPreferences sharedPreferences = getSharedPreferences("theme", 0);
                        Editor editor = sharedPreferences.edit();
                        editor.putString(THEME, "2");
                        editor.apply();

                    }
                    if(checkedId == R.id.btnDark){

                        moon.setVisibility(View.VISIBLE);
                        default_settings.setVisibility(View.GONE);
                        sun.setVisibility(View.GONE);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SharedPreferences sharedPreferences = getSharedPreferences("theme", 0);
                        Editor editor = sharedPreferences.edit();
                        editor.putString(THEME, "1");
                        editor.apply();

                    }
                    if(checkedId == R.id.btnDefault){
                        moon.setVisibility(View.GONE);
                        default_settings.setVisibility(View.VISIBLE);
                        sun.setVisibility(View.GONE);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        SharedPreferences sharedPreferences = getSharedPreferences("theme", 0);
                        Editor editor = sharedPreferences.edit();
                        editor.putString(THEME, "0");
                        editor.apply();

                    }
                }

        });

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("theme", 0);
        String Theme = sharedPreferences.getString(THEME, "");

        if(Theme.equals("2")){
            materialButtonToggleGroup.check(R.id.btnLight);
            sun.setVisibility(View.VISIBLE);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            moon.setVisibility(View.GONE);
            default_settings.setVisibility(View.GONE);
        }

        if(Theme.equals("1")){
            materialButtonToggleGroup.check(R.id.btnDark);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            moon.setVisibility(View.VISIBLE);
            default_settings.setVisibility(View.GONE);
            sun.setVisibility(View.GONE);

        }
        if(Theme.equals("0")){
            materialButtonToggleGroup.check(R.id.btnDefault);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            default_settings.setVisibility(View.VISIBLE);
            moon.setVisibility(View.GONE);
            sun.setVisibility(View.GONE);
        }


    }


    @Override
    public void onBackPressed() {
        finish();

    }
}
