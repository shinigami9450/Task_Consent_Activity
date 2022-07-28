package com.example.task_consent_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.ads.consent.*
import java.net.MalformedURLException
import java.net.URL

class ConsentActivity : AppCompatActivity() {

    private lateinit var form: ConsentForm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)

        getConsentStatus()
    }

    private fun getConsentStatus() {

        ConsentInformation.getInstance(this).addTestDevice("")

        ConsentInformation.getInstance(this).debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA

        val consentInformation = ConsentInformation.getInstance(this)
        val publisherIds = arrayOf("pub-3883520076643562")
        consentInformation.requestConsentInfoUpdate(
            publisherIds,
            object : ConsentInfoUpdateListener {

                override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                    Log.d("status","inside status $consentStatus")
                    if(ConsentInformation.getInstance(baseContext).isRequestLocationInEeaOrUnknown){
                        Log.d("if","inside if")
                        when(consentStatus){

                            ConsentStatus.UNKNOWN -> displayConsentForm()

                            ConsentStatus.NON_PERSONALIZED -> {
                                val intent = Intent(this@ConsentActivity, SplashActivity::class.java)
                                intent.putExtra("isPersonalized", false)
                                startActivity(intent)
                            }

                            ConsentStatus.PERSONALIZED -> {
                                val intent = Intent(this@ConsentActivity, SplashActivity::class.java)
                                intent.putExtra("isPersonalized", true)
                                startActivity(intent)
                            }
                        }
                    }
                    else
                    {
                        Log.d("else","inside else")
                    }
                }

                override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                    // User's consent status failed to update.
                    //Log.d("fail","consent fail $errorDescription")
                    Toast.makeText(this@ConsentActivity,"Consent form failed to load...", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@ConsentActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            })

    }

    private fun displayConsentForm() {
        //Log.d("display","inside display")
        var privacyUrl: URL? = null
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = URL("https://www.google.com")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            // Handle error.
        }
        form = ConsentForm.Builder(this, privacyUrl)
            .withListener(object : ConsentFormListener() {
                override fun onConsentFormLoaded() {
                    // Consent form loaded successfully.
                    form.show()
                }

                override fun onConsentFormOpened() {
                    // Consent form was displayed.
                    //form.show()
                }

                override fun onConsentFormClosed(
                    consentStatus: ConsentStatus, userPrefersAdFree: Boolean
                ) {
                    // Consent form was closed.
                    when(consentStatus){
                        ConsentStatus.PERSONALIZED -> {
                            val intent = Intent(this@ConsentActivity, SplashActivity::class.java)
                            intent.putExtra("isPersonalized", true)
                            startActivity(intent)
                        }

                        ConsentStatus.NON_PERSONALIZED -> {
                            val intent = Intent(this@ConsentActivity, SplashActivity::class.java)
                            intent.putExtra("isPersonalized", false)
                            startActivity(intent)
                        }

                        ConsentStatus.UNKNOWN -> displayConsentForm()
                    }
                }

                override fun onConsentFormError(errorDescription: String) {
                    // Consent form error.
                }
            })
            .withPersonalizedAdsOption()
            .withNonPersonalizedAdsOption()
            .withAdFreeOption()
            .build()

        form.load()
    }

}