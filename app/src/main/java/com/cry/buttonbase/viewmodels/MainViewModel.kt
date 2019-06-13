package com.cry.buttonbase.viewmodels

import android.app.Application
import android.media.MediaPlayer
import com.cry.buttonbase.core.utils.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cry.buttonbase.models.Repositories.IpRepository
import com.google.firebase.analytics.FirebaseAnalytics

class MainViewModel(application : Application) : ViewModelBase(application)
{
    val app = application
    var adManager = AdManager()
    var count = 1
    var steps = 3

    private var mButtonPressed = MutableLiveData<Boolean>()
    val ButtonPressed : LiveData<Boolean> get() = mButtonPressed

    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(app)

    var mp = MediaPlayer()

    init {

        IpRepository().getIpData {
            if (it.country_eu){
                Log.d("GDPR", "Should show GDPR consent request")
            }
            else
            {
                Log.d("GDPR", "NOT EEU COUNTRY")
            }
        }

        adManager.initializeMobileAds(app)
        //initialize interstitial ad
        adManager.createInterstitial()

    }

    fun btnSoundPressed()
    {
        if (mp.isPlaying) {
            stopSound()
        }
        else {
            playSound()
        }
    }

    fun btnFollowFbPressed()
    {
        SocialMediaManager().openFacebook(app)
    }

    fun btnFollowTwttrPressed()
    {
        SocialMediaManager().openTwitter(app)
    }

    fun btnFollowIgPressed()
    {
        SocialMediaManager().openInstagram(app)
    }

    fun btnShareSMPressed()
    {
        SocialMediaManager().shareSocial(app)
    }

    private fun playSound()
    {
        count++
        mp = MediaPlayer()
        val afd = app.assets.openFd("sound.mp3")
        mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        mp.setOnCompletionListener {
            try {
                mButtonPressed.postValue(false)
                mp.stop()
            } catch (ex: Exception) {
                Log.wtf("BitmapError", "Here's the OutOfMemoryException... " + ex.message)
                ex.printStackTrace()
            }
        }
        mp.prepare()
        mp.start()

        mButtonPressed.postValue(true)

        if ((count%steps) == 0)
            adManager.showInterstitialAd()
    }

    private fun stopSound()
    {
        mButtonPressed.postValue(false)
        mp.stop()
    }

}