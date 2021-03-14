package com.margsapp.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class CustomiseActivity extends AppCompatActivity {

    public static final String THEME = "0";
    MaterialButtonToggleGroup materialButtonToggleGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);




        materialButtonToggleGroup = findViewById(R.id.btg_theme);
        loadData();
        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                    if(checkedId == R.id.btnLight){

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


                        SharedPreferences sharedPreferences = getSharedPreferences("theme", 0);
                        Editor editor = sharedPreferences.edit();
                        editor.putString(THEME, "2");
                        editor.apply();

                    }
                    if(checkedId == R.id.btnDark){


                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SharedPreferences sharedPreferences = getSharedPreferences("theme", 0);
                        Editor editor = sharedPreferences.edit();
                        editor.putString(THEME, "1");
                        editor.apply();

                    }
                    if(checkedId == R.id.btnDefault){

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

        }

        if(Theme.equals("1")){
            materialButtonToggleGroup.check(R.id.btnDark);

        }
        if(Theme.equals("0")){
            materialButtonToggleGroup.check(R.id.btnDefault);

        }

    }

    private void updateViews() {


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CustomiseActivity.this, edit_profile.class));

    }
}
