package com.example.task_consent_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

const val AD_UNIT_ID = "ca-app-pub-3883520076643562/6777453655"

class SplashActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private var isPersonalized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        isPersonalized = intent.getBooleanExtra("isPersonalized",false)

        //Log.d("splash","inside splash activity $isPersonalized")

        MobileAds.initialize(this)
        loadAds(isPersonalized)
    }

    private fun loadAds(isPersonalized: Boolean)
    {

        val adRequest: AdRequest
        if(isPersonalized)
        {
            adRequest = AdRequest.Builder().build()
        }
        else
        {
            val extras = Bundle()
            extras.putString("npa", "1")

            adRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()
        }
        InterstitialAd.load(this, AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                //Log.d("error123", "here $adError")
                mInterstitialAd = null
                Toast.makeText(this@SplashActivity,"Ad failed to load...",Toast.LENGTH_LONG).show()

                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                //Log.d("loaded123", "Ad was loaded.")
                mInterstitialAd = interstitialAd

                mInterstitialAd?.show(this@SplashActivity)

                mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.

                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        })

    }


}