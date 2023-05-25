package com.myprojects.advertiseapi

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.myprojects.advertiseapi.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)

        val apiUrl = "https://resemblant-workload.000webhostapp.com/get_ads.txt"

        Thread {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Get the response from the API
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    runOnUiThread {
                        try {
                            val jsonObject = JSONObject(response.toString())
                            val resultObject = jsonObject.optJSONObject("result")
                            if (resultObject != null) {
                                val bannerAdUnitId = resultObject.optString("google_banner")
                                val interstitialAdUnitId = resultObject.optString("google_interstitial")

                                val openappAdUnitId = resultObject.optString("google_ao")

                                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("openappAdUnitId", openappAdUnitId)
                                editor.apply()

                                val nativeAdUnitId = resultObject.optString("google_native")

                                binding.btnNativeAd.setOnClickListener {

                                }

                                if (!bannerAdUnitId.isNullOrEmpty()) {
                                    mAdView = AdView(this)
                                    mAdView.adUnitId = bannerAdUnitId
                                    mAdView.adSize = AdSize.BANNER

                                    val adContainer = findViewById<LinearLayout>(R.id.adContainer)
                                    adContainer.addView(mAdView)

                                    val adRequest = AdRequest.Builder().build()
                                    mAdView.loadAd(adRequest)
                                }

                                binding.button.setOnClickListener {
                                    var adRequest = AdRequest.Builder().build()

                                    InterstitialAd.load(this,interstitialAdUnitId, adRequest, object : InterstitialAdLoadCallback() {
                                        override fun onAdFailedToLoad(adError: LoadAdError) {
                                            mInterstitialAd = null
                                        }

                                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                            mInterstitialAd = interstitialAd
                                        }
                                    })

                                    if (mInterstitialAd != null) {
                                        mInterstitialAd?.show(this)
                                    } else {
                                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                                    }
                                }

                            }
                        } catch (e: Exception) {
                            // Handle any exceptions that occurred during the API call
                            Log.e("network", e.localizedMessage)
                        }
                    }
                } else {
                    throw Exception("API request failed with response code: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}