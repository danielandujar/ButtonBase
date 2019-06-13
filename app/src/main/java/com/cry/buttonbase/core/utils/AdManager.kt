package com.cry.buttonbase.core.utils

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.InterstitialAd
import com.cry.buttonbase.R
import com.google.android.gms.ads.AdRequest


class AdManager
{
    // Static fields are shared between all instances.
    lateinit var interstitial : InterstitialAd
    lateinit var app : Application

    fun initializeMobileAds(app : Application)
    {
        this.app = app
        MobileAds.initialize(app)
    }

    fun createInterstitial() {
        // Create an ad.
        interstitial = InterstitialAd(app)
        interstitial.adUnitId = app.resources.getString(R.string.interstitial_ad_id)
        // Create ad request.
        val adRequest : AdRequest = AdRequest.Builder()
            .addTestDevice("803D7394B359FA9EBA5C03A19520CC89")
            .build()

        interstitial.loadAd(adRequest)
    }

    fun  getInterstitialAd() : InterstitialAd {
        return interstitial
    }

    fun showInterstitialAd()
    {
        interstitial.show()
        createInterstitial()
    }

}