package com.margsapp.messageium.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messageium.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.Objects;

public class Wallpaper extends AppCompatActivity {


    private static final String WALLPAPER = "WALLPAPER";
    ImageView dark1,dark2,dark3,light1,light2,light3;

    CardView default_wallpaper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        dark1 = findViewById(R.id.dark1);
        dark2 = findViewById(R.id.dark2);
        dark3 = findViewById(R.id.dark3);

        light1 = findViewById(R.id.light1);
        light2 = findViewById(R.id.light2);
        light3 = findViewById(R.id.light3);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.ChooseWallpaper));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);

            }
        });

        SlidrInterface slidrInterface = Slidr.attach(this);

        default_wallpaper = findViewById(R.id.default_wallpaper);

        default_wallpaper.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                    default_wallpaper.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                }else {
                    default_wallpaper.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });


        default_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(Wallpaper.this)
                        .setTitle("Are you sure you want to keep default wallpaper?")
                        .setMessage("Any mode can be used")
                        .isCancellable(true)
                        .setNegativeText(getResources().getString(R.string.no))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do nothing
                            }
                        })
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(WALLPAPER, "null");
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

        dark1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(Wallpaper.this)
                        .setTitle("Are you sure you want to keep this as wallpaper?")
                        .setMessage("Dark mode is recommended")
                        .isCancellable(true)
                        .setNegativeText(getResources().getString(R.string.no))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do nothing
                            }
                        })
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(WALLPAPER, "dark1");
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

        dark2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(Wallpaper.this)
                        .setTitle("Are you sure you want to keep this as wallpaper?")
                        .setMessage("Dark mode is recommended")
                        .isCancellable(true)
                        .setNegativeText(getResources().getString(R.string.no))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do nothing
                            }
                        })
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(WALLPAPER, "dark2");
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

        dark3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(Wallpaper.this)
                        .setTitle("Are you sure you want to keep this as wallpaper?")
                        .setMessage("Dark mode is recommended")
                        .isCancellable(true)
                        .setNegativeText(getResources().getString(R.string.no))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do nothing
                            }
                        })
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(WALLPAPER, "dark3");
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

        light1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(Wallpaper.this)
                        .setTitle("Are you sure you want to keep this as wallpaper?")
                        .setMessage("Light mode is recommended")
                        .isCancellable(true)
                        .setNegativeText(getResources().getString(R.string.no))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do nothing
                            }
                        })
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(WALLPAPER, "light1");
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

        light2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(Wallpaper.this)
                        .setTitle("Are you sure you want to keep this as wallpaper?")
                        .setMessage("Light mode is recommended")
                        .isCancellable(true)
                        .setNegativeText(getResources().getString(R.string.no))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do nothing
                            }
                        })
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(WALLPAPER, "light2");
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

        light3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(Wallpaper.this)
                        .setTitle("Are you sure you want to keep this as wallpaper?")
                        .setMessage("Light mode is recommended")
                        .isCancellable(true)
                        .setNegativeText(getResources().getString(R.string.no))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do nothing
                            }
                        })
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(WALLPAPER, "light3");
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

    }

}
