package com.margsapp.messageium.AppDetails

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.interstitial.InterstitialAd
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.margsapp.messageium.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import com.margsapp.messageium.Main.MainActivity
import com.margsapp.messageium.databinding.ActivityPrivacyBinding
import com.margsapp.messageium.databinding.ActivityTermsConditionsBinding


class privacyActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

    private lateinit var binding: ActivityPrivacyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.privacyPage.loadUrl("https://margsglobal.weebly.com/messenger-privacy-policy.html")
        intent = getIntent()
        var method = intent.getStringExtra("method")
        MobileAds.initialize(this) { initializationStatus: InitializationStatus? -> }
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-5615682506938042/1799098378",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i(TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.message)
                    mInterstitialAd = null
                }
            })
        binding.agree.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@privacyActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            if (mInterstitialAd != null) {
                mInterstitialAd!!.show(this@privacyActivity)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
            finish()
        })
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}