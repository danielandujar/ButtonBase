package com.cry.buttonbase.viewmodels

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.cry.buttonbase.core.utils.*
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cry.buttonbase.models.Repositories.IpRepository
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.File

class MainViewModel(application : Application) : ViewModelBase(application)
{
    val app = application
    var adManager = AdManager()
    var count = 1
    var steps = 3

    private var mButtonPressed = MutableLiveData<Boolean>()
    val ButtonPressed : LiveData<Boolean> get() = mButtonPressed

    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(app)
    val firebaseUtils = FirebaseUtils()
    var fileExists = false

    private var _fileExistData = MutableLiveData<Boolean>()
    val fileExistData : LiveData<Boolean> get() = _fileExistData

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

        val localFile = File(app.filesDir, "sound.mp3")

        fileExists = localFile.isFile
        _fileExistData.postValue(fileExists)

        val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork : NetworkInfo? = cm.activeNetworkInfo
        val isConnected : Boolean = activeNetwork?.isConnected == true

        if (isConnected) {
            firebaseUtils.authUser(app) {
                if (it) {
                    if (!fileExists)
                        firebaseUtils.getFile { downloaded ->
                            fileExists = downloaded
                            _fileExistData.postValue(fileExists)
                        }

                }
            }
        }
        else {
            if (!fileExists)
            {
                Toast.makeText(app, "Unable to download content, no Network connection!", Toast.LENGTH_LONG)
                    .show()
            }
        }
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
        if (fileExists) {
            count++
            mp = MediaPlayer()

            val file = File(app.filesDir, "sound.mp3")
            try {
                mp.setDataSource(file.path)
                mp.setOnCompletionListener {
                    try {
                        mButtonPressed.postValue(false)
                        mp.stop()
                    } catch (ex: Exception) {
                        Log.wtf("BitmapError", "Here's the OutOfMemoryException... " + ex.message)
                        ex.printStackTrace()
                    }
                }

                //val afd = app.assets.openFd("sound.mp3")
                //mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

                mp.prepare()

            }
            catch (e : java.lang.Exception)
            {
                e.printStackTrace()
            }
            mp.start()

            mButtonPressed.postValue(true)

            if ((count % steps) == 0)
                adManager.showInterstitialAd()
        }
    }

    private fun stopSound()
    {
        mButtonPressed.postValue(false)
        mp.stop()
    }

}