package com.margsapp.messenger.AppDetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.margsapp.messenger.AppDetails.Terms_ConditionsActivity
import com.margsapp.messenger.AppDetails.privacyActivity
import com.margsapp.messenger.R
import kotlinx.android.synthetic.main.activity_terms__conditions.*

class Terms_ConditionsActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms__conditions)


        t_cpage.loadUrl("https://margsglobal.weebly.com/messenger-tc.html")
        intent = getIntent()
        var method = intent.getStringExtra("method")
        MobileAds.initialize(this) { initializationStatus: InitializationStatus? -> }
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-5615682506938042/1159728726",
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
         agree.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@Terms_ConditionsActivity, privacyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("method", method)
            startActivity(intent)
            if (mInterstitialAd != null) {
                mInterstitialAd!!.show(this@Terms_ConditionsActivity)
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