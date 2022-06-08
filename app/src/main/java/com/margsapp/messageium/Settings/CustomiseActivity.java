package com.margsapp.messageium.Settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messageium.Authentication.StartActivity;
import com.margsapp.messageium.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.Locale;
import java.util.Objects;

public class CustomiseActivity extends AppCompatActivity {

    public static final String THEME = "0";
    MaterialButtonToggleGroup materialButtonToggleGroup;

    ImageView sun,default_settings,moon;

    CardView lang_card;

    int languageid;

    CardView set_wallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        SlidrInterface slidrInterface = Slidr.attach(this);

        sun = findViewById(R.id.sun_img);
        moon = findViewById(R.id.moon_img);
        default_settings = findViewById(R.id.default_img);
        sun.setVisibility(View.GONE);
        moon.setVisibility(View.GONE);
        default_settings.setVisibility(View.VISIBLE);

        lang_card = findViewById(R.id.lang_card);
        set_wallpaper = findViewById(R.id.set_wallpaper);

        SharedPreferences preferences = getSharedPreferences("lang_settings", Activity.MODE_PRIVATE);
        languageid = preferences.getInt("langid", 0);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.customize));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);

            }
        });


        materialButtonToggleGroup = findViewById(R.id.btg_theme);
        materialButtonToggleGroup.check(R.id.btnDefault);

        loadData();
        materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {

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
            });

        lang_card.setOnClickListener(v -> showLanguageDialog());


        set_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomiseActivity.this,setWallpaper.class));
                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
            }
        });

    }

    private void showLanguageDialog() {

        final String[] langitems = {"English (Default)","தமிழ்", "हिंदी","తెలుగు"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.choose_language));

        dialog.setSingleChoiceItems(langitems, languageid, (dialog1, i) -> {
            if(i == 0){
                setLocale("en",0);

                iOSDialog.Builder
                        .with(CustomiseActivity.this)
                        .setTitle("App will restart now!")
                        .setMessage("To update language settings the app will restart now")
                        .setNegativeText("OK")
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                Intent mStartActivity = new Intent(getApplicationContext(), StartActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                System.exit(0);
                            }
                        })
                        .setNegativeTextColor(getResources().getColor(R.color.red))

                        .isCancellable(false)
                        .build()
                        .show();



            }

            else if (i == 1){
                setLocale("ta",1);

                iOSDialog.Builder
                        .with(CustomiseActivity.this)
                        .setTitle("App will restart now!")
                        .setMessage("To update language settings the app will restart now")
                        .setNegativeText("OK")
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                Intent mStartActivity = new Intent(getApplicationContext(), StartActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                System.exit(0);
                            }
                        })
                        .setNegativeTextColor(getResources().getColor(R.color.red))

                        .isCancellable(false)
                        .build()
                        .show();

            }

            else if(i==2){

                setLocale("hi",2);

                iOSDialog.Builder
                        .with(CustomiseActivity.this)
                        .setTitle("App will restart now!")
                        .setMessage("To update language settings the app will restart now")
                        .setNegativeText("OK")
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                Intent mStartActivity = new Intent(getApplicationContext(), StartActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                System.exit(0);
                            }
                        })
                        .setNegativeTextColor(getResources().getColor(R.color.red))

                        .isCancellable(false)
                        .build()
                        .show();

            }
            else if(i == 3){
                setLocale("te",3);

                iOSDialog.Builder
                        .with(CustomiseActivity.this)
                        .setTitle("App will restart now!")
                        .setMessage("To update language settings the app will restart now")
                        .setNegativeText("OK")
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                Intent mStartActivity = new Intent(getApplicationContext(), StartActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                System.exit(0);
                            }
                        })
                        .setNegativeTextColor(getResources().getColor(R.color.red))

                        .isCancellable(false)
                        .build()
                        .show();

            }

            dialog1.dismiss();
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void setLocale(String lang, int langid) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("lang_settings",MODE_PRIVATE).edit();
        editor.putString("lang",lang);
        editor.putInt("langid",langid);
        editor.apply();







    }

    private void loadData() {
        SharedPreferences preferences = getSharedPreferences("lang_settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("lang","");
        int langid = preferences.getInt("langid", 0);
        setLocale(language, langid);




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
        overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);

    }
}
