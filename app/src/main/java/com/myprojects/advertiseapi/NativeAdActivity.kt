package com.myprojects.advertiseapi

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.myprojects.advertiseapi.databinding.ActivityNativeAdBinding

class NativeAdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNativeAdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNativeAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)

        loadNativeAd()
    }

    private fun loadNativeAd() {

        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad : NativeAd ->
                val inflater = parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                        as LayoutInflater
                val adView = layoutInflater
                    .inflate(R.layout.ad_native, null) as NativeAdView

                populate(adView, ad)
                val adContainer = findViewById<FrameLayout>(R.id.frameNativeAd)
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder()
                .build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

    }

    fun populate(parent: ViewGroup, ad: NativeAd) {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val adView = inflater.inflate(R.layout.ad_native, parent) as NativeAdView

        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = ad.headline
        adView.mediaView!!.setMediaContent(ad.mediaContent!!)

        if (ad.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = ad.body
        }
        if (ad.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = ad.callToAction
        }
        if (ad.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                ad.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        if (ad.price == null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView).text = ad.price
        }
        if (ad.store == null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView).text = ad.store
        }
        if (ad.starRating == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = ad.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }
        if (ad.advertiser == null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = ad.advertiser
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        adView.setNativeAd(ad)
        parent.removeAllViews()
        parent.addView(adView)
    }
}