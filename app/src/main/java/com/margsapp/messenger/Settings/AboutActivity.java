package com.margsapp.messenger.Settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messenger.BuildConfig;
import com.margsapp.messenger.Model.AppVersion;
import com.margsapp.messenger.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";
    TextView app_version;
    String versionName = BuildConfig.VERSION_NAME;

    private InterstitialAd mInterstitialAd;

    DatabaseReference firebaseDatabase;

    AppCompatButton checkupdate;

    public String appString;
    SwitchCompat Swicth_authenticate,Read_Recipients;



    public String Authentication = "0";
    boolean switchAuthentication;
    boolean readRecipients;
    public String readrecipients = "0";



    public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";

    public static final String SWITCH2 = "switch2";
    public static final String TEXT1 = "text";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SlidrInterface slidrInterface = Slidr.attach(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            if(Read_Recipients.isChecked()){
                SharedPreferences sharedPreferences = getSharedPreferences("ReadRecipents",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT1, "1");
                editor.putBoolean(SWITCH2, Read_Recipients.isChecked());
                editor.apply();

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AboutActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

                Intent intent = new Intent(AboutActivity.this, edit_profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);

            }else if(!Read_Recipients.isChecked()){

                SharedPreferences sharedPreferences = getSharedPreferences("ReadRecipents",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT1, "0");
                editor.putBoolean(SWITCH2, Read_Recipients.isChecked());
                editor.apply();

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AboutActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

                Intent intent = new Intent(AboutActivity.this, edit_profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);

            }
            if(Swicth_authenticate.isChecked()) {
                Biometric();

            } else if(!Swicth_authenticate.isChecked()) {
                SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT, "0");
                editor.putBoolean(SWITCH1, Swicth_authenticate.isChecked());
                editor.apply();

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AboutActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

                Intent intent = new Intent(AboutActivity.this, edit_profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }

        });

        checkupdate = findViewById(R.id.check_update);
        Swicth_authenticate = findViewById(R.id.Swtich_authentication);
        Read_Recipients = findViewById(R.id.readRecipts_Switch);

        Read_Recipients.setChecked(true);


        /*

        MobileAds.initialize(this, initializationStatus -> {
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

        AdView mAdView = findViewById(R.id.adView);

        mAdView.loadAd(adRequest);

         */

        loadData();
        updateViews();



        app_version = findViewById(R.id.app_version);


        app_version.setText(versionName);

        appString = app_version.getText().toString();

        checkupdate.setOnClickListener(v -> {
            firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("AppVersion");
            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AppVersion appVersion = snapshot.getValue(AppVersion.class);
                    assert appVersion != null;


                    if (appString.equals(appVersion.getAppversion())) {
                        Toast.makeText(AboutActivity.this, getResources().getString(R.string.upto_date), Toast.LENGTH_SHORT).show();

                    } else {

                        iOSDialog.Builder
                                .with(AboutActivity.this)
                                .setTitle(getResources().getString(R.string.check_update))
                                .setMessage(getString(R.string.like_to_update))
                                .setPositiveText(getResources().getString(R.string.yes))
                                .setPostiveTextColor(getResources().getColor(R.color.red))
                                .setNegativeText(getResources().getString(R.string.no))
                                .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                                .onPositiveClicked(new iOSDialogListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        Uri uri = Uri.parse("https://margsweb.wixsite.com/messenger");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                })
                                .onNegativeClicked(new iOSDialogListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        //Do Nothing
                                    }
                                })
                                .isCancellable(true)
                                .build()
                                .show();


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });
        });
    }



    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
        Authentication = sharedPreferences.getString(TEXT, "");
        switchAuthentication = sharedPreferences.getBoolean(SWITCH1,false);

        SharedPreferences sharedPreferences1 = getSharedPreferences("ReadRecipents",0);
        readrecipients = sharedPreferences1.getString(TEXT1,"");
        readRecipients = sharedPreferences1.getBoolean(SWITCH2,false);

    }

    public void updateViews(){
        Swicth_authenticate.setChecked(switchAuthentication);
        Read_Recipients.setChecked(readRecipients);
    }

    private void Biometric(){
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {


            // this means we can use biometric sensor
            case androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS:
                SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT, "1");
                editor.putBoolean(SWITCH1, Swicth_authenticate.isChecked());
                editor.apply();

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AboutActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

                Intent intent = new Intent(AboutActivity.this, edit_profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

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
                Toast.makeText(this, "Error code 0x08080103 There's no password for this device.", Toast.LENGTH_SHORT).show();
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

        if(Read_Recipients.isChecked()){
            SharedPreferences sharedPreferences = getSharedPreferences("ReadRecipents",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT1, "1");
            editor.putBoolean(SWITCH2, Read_Recipients.isChecked());
            editor.apply();

            if (mInterstitialAd != null) {
                mInterstitialAd.show(AboutActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

            Intent intent = new Intent(AboutActivity.this, edit_profile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);

        }else if(!Read_Recipients.isChecked()){

            SharedPreferences sharedPreferences = getSharedPreferences("ReadRecipents",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT1, "0");
            editor.putBoolean(SWITCH2, Read_Recipients.isChecked());
            editor.apply();

            if (mInterstitialAd != null) {
                mInterstitialAd.show(AboutActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

            Intent intent = new Intent(AboutActivity.this, edit_profile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);

        }

        if(Swicth_authenticate.isChecked()) {
            Biometric();
        } else if(!Swicth_authenticate.isChecked()) {
            SharedPreferences sharedPreferences = getSharedPreferences("Authentication",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT, "0");
            editor.putBoolean(SWITCH1, Swicth_authenticate.isChecked());
            editor.apply();

            if (mInterstitialAd != null) {
                mInterstitialAd.show(AboutActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

            Intent intent = new Intent(AboutActivity.this, edit_profile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);



        }

    }

    private void status(String status){
        FirebaseUser firebaseUserStatus = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUserStatus != null;
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