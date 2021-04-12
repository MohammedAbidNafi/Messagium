package com.margsapp.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    EditText username, email, password;
    AppCompatButton btnRegister;

    FirebaseAuth mAuth;
    DatabaseReference reference;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/8064581256", adRequest, new InterstitialAdLoadCallback() {
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView mAdView = findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.passowrd);
        btnRegister = findViewById(R.id.register);

        btnRegister.setOnClickListener(v -> {
            String txt_username = Objects.requireNonNull(username.getText()).toString();
            String txt_email = Objects.requireNonNull(email.getText()).toString();
            String txt_password = Objects.requireNonNull(password.getText()).toString();

            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
            String timestamp = simpleDateFormat.format(calendar.getTime());

            ConnectivityManager manager =(ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                    //we have WIFI
                    if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_email)) {
                        Toast.makeText(RegisterActivity.this, "Fill all the details error code 0x08020102", Toast.LENGTH_SHORT).show();

                    } else if (txt_password.length() < 5) {
                        Toast.makeText(RegisterActivity.this, "Password must be atleast 5 charecters error code 0x08020103", Toast.LENGTH_SHORT).show();
                    } else {
                        register(txt_username, txt_email, txt_password,timestamp);
                    }

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(RegisterActivity.this);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }
                if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                    //we have cellular data
                    if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_email)) {
                        Toast.makeText(RegisterActivity.this, "Fill all the details error code 0x08020102", Toast.LENGTH_SHORT).show();

                    } else if (txt_password.length() < 5) {
                        Toast.makeText(RegisterActivity.this, "Password must be atleast 5 charecters error code 0x08020103", Toast.LENGTH_SHORT).show();
                    } else {
                        register(txt_username, txt_email, txt_password,timestamp);
                    }

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(RegisterActivity.this);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }
            } else{
                Toast.makeText(RegisterActivity.this,"Opps! Looks like theres no internet connection.",Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void register(String txt_username, String txt_email, String txt_password, String timestamp) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", txt_username);
                        hashMap.put("imageURL", "default");
                        hashMap.put("joined_on",timestamp);

                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, Terms_ConditionsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Problem Occurred while registering error code 0x08020101", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
