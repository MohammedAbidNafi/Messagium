package com.margsapp.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.AppVersion;

import java.net.Authenticator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";
    TextView app_version;
    String versionName = BuildConfig.VERSION_NAME;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    DatabaseReference firebaseDatabase;
    FirebaseUser firebaseUser;

    AppCompatButton checkupdate;

    public String appString;
    SwitchCompat Swicth_authenticate;

    AppCompatButton test_btn;

    public String Authentication = "0";
    boolean switchAuthentication;


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, edit_profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        checkupdate = findViewById(R.id.check_update);
        Swicth_authenticate = findViewById(R.id.Swtich_authentication);
        test_btn = findViewById(R.id.test_btn);

        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Swicth_authenticate.isChecked()) {
                    Biometric();
                }else if(!Swicth_authenticate.isChecked()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TEXT, "0");
                    editor.putBoolean(SWITCH1, Swicth_authenticate.isChecked());
                    editor.apply();


                    Toast.makeText(AboutActivity.this, "Biometric is off",Toast.LENGTH_SHORT).show();
                }
            }
        });



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/6089487745", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

        loadData();
        updateViews();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, edit_profile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AboutActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

        app_version = findViewById(R.id.app_version);


        app_version.setText(versionName);

        appString = app_version.getText().toString();

        checkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("AppVersion");
                firebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AppVersion appVersion = snapshot.getValue(AppVersion.class);
                        assert appVersion != null;


                        if (appString.equals(appVersion.getAppversion())) {
                            Toast.makeText(AboutActivity.this, "This app is upto date", Toast.LENGTH_SHORT).show();

                        } else {

                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(AboutActivity.this);
                            dialog.setMessage("There's a new version of this app would you like to update?");
                            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Uri uri = Uri.parse("http://www.google.com");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });

                            dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Dont do anything
                                }
                            });
                            androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
                            alertDialog.show();


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });
            }


        });
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
        Authentication = sharedPreferences.getString(TEXT, "");
        switchAuthentication = sharedPreferences.getBoolean(SWITCH1,false);
    }

    public void updateViews(){
        Swicth_authenticate.setChecked(switchAuthentication);
    }

    private void Biometric(){
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {


            // this means we can use biometric sensor
            case androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS:
                SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT, "1");
                editor.putBoolean(SWITCH1, Swicth_authenticate.isChecked());
                editor.apply();

                Toast.makeText(this, "Succesfully Activated", Toast.LENGTH_SHORT).show();
                break;

            // this means that the device doesn't have fingerprint sensor
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Swicth_authenticate.setChecked(false);
                Toast.makeText(this, "Error code 0x08080101 Authentication failed there's no Fingerprint Reader in your device.", Toast.LENGTH_SHORT).show();
                break;

            // this means that biometric sensor is not available
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Swicth_authenticate.setChecked(false);
                Toast.makeText(this, "Error code 0x08080102 Authentication failed biometric system not found.", Toast.LENGTH_SHORT).show();
                break;

            // this means that the device doesn't contain your fingerprint
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Swicth_authenticate.setChecked(false);
                Toast.makeText(this, "Error code 0x08080103 Theres no fingerprint saved.", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AboutActivity.this, edit_profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private void status(String status){
        FirebaseUser firebaseUserStatus = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference statusdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserStatus.getUid());

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy hh:mm aa");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        hashMap.put("lastseen", timestamp);

        statusdatabaseReference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }
}