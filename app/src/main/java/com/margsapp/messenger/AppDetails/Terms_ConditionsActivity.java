package com.margsapp.messenger.AppDetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.margsapp.messenger.R;

public class Terms_ConditionsActivity extends AppCompatActivity {

    AppCompatButton agree;

    private InterstitialAd mInterstitialAd;

    Intent intent;
    String method;

    private static final String TAG = "MainActivity";

    WebView t_c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms__conditions);

        agree = findViewById(R.id.agree);

        t_c = findViewById(R.id.t_cpage);
        t_c.loadUrl("https://margsglobal.weebly.com/messenger-tc.html");


        intent = getIntent();
        method = intent.getStringExtra("method");

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/1159728726", adRequest, new InterstitialAdLoadCallback() {
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




        agree.setOnClickListener(v -> {
            Intent intent = new Intent(Terms_ConditionsActivity.this, privacyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("method",method);
            startActivity(intent);
            if (mInterstitialAd != null) {
                mInterstitialAd.show(Terms_ConditionsActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

            finish();
        });
    }

}