package com.cry.buttonbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.cry.buttonbase.viewmodels.MainViewModel
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.analytics.FirebaseAnalytics
import android.util.StatsLog.logEvent
import android.app.Dialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.media.RingtoneManager
import android.provider.MediaStore
import android.provider.SyncStateContract.Helpers.update
import java.io.File
import android.content.ContentUris
import android.R.attr.path
import android.provider.SyncStateContract.Helpers.update
import android.content.ContentValues






class MainActivity : AppCompatActivity() {

    lateinit var viewModel : MainViewModel
    lateinit var buttonStateObserver : Observer<Boolean>
    lateinit var fileStateObserver : Observer<Boolean>

    val PREFS_NAME = "preferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            modalPanel.elevation = 5f
            pbLoading.elevation = 6f
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        buttonStateObserver = Observer{
            btnSound.isActivated = it
        }
        viewModel.ButtonPressed.observe(this, buttonStateObserver)

        fileStateObserver = Observer {
            if (it){
                enableButton()
            }
        }
        viewModel.fileExistData.observe(this, fileStateObserver)

        setupAds()

        setupRateDialog()
    }

    fun enableButton()
    {
        //stop the spinner
        pbLoading.visibility = View.INVISIBLE
        modalPanel.visibility = View.INVISIBLE
        //btnSetRingtone.visibility = View.VISIBLE
    }

    fun setupRateDialog()
    {
        var settings = getSharedPreferences(PREFS_NAME, 0)
        var useCount: Int = Integer.parseInt(settings.getString("UseCount", "0")!! )
        val neverAskAgain = settings.getString("NeverAsk", "false")
        if (neverAskAgain != "true" && useCount >= 3) {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.rate_dialog)
            //dialog.getWindow().setBackgroundDrawable(
            //        new ColorDrawable(android.graphics.Color.TRANSPARENT));

            val btnRate = dialog.findViewById(R.id.rateOK) as Button
            val btnNeverAsk = dialog.findViewById(R.id.rateDeny) as TextView
            val btnRateLater = dialog.findViewById(R.id.rateLater) as TextView

            btnRate.setOnClickListener(View.OnClickListener { v ->
                val uri = Uri.parse("market://details?id=" + v.context.packageName)
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                try {
                    startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + v.context.packageName)
                        )
                    )
                }

                Log.i("RateDialog", "Got to GooglePlay! for rate, maybe?")
            })
            btnRateLater.setOnClickListener {
                val useCount = 0
                val settings = getSharedPreferences(PREFS_NAME, 0)
                val editor = settings.edit()
                editor.putString("UseCount", useCount.toString())
                editor.apply()
                dialog.dismiss()

                Log.i("RateDialog", "Choosed Ask Later")
            }
            btnNeverAsk.setOnClickListener { dialog.dismiss() }

            dialog.setOnDismissListener(DialogInterface.OnDismissListener {
                val settings = getSharedPreferences(PREFS_NAME, 0)
                val editor = settings.edit()
                editor.putString("NeverAsk", "true")
                editor.apply()

                Log.i("RateDialog", "Choosed Never Ask Again ")
            })
            dialog.show()
            Log.i("RateDialog", "Rate Dialog Showed")
        } else if (neverAskAgain != "true") {
            useCount++
            settings = getSharedPreferences(PREFS_NAME, 0)
            val editor = settings.edit()
            editor.putString("UseCount", useCount!!.toString())
            editor.apply()
        }
    }

    fun btnSoundClick(v : View)
    {
        viewModel.btnSoundPressed()
    }

    fun btnRingtoneClicked(v : View)
    {
        setRingtone(this, File(filesDir, "sound.mp3").path)
    }

    fun btnFollowFbClicked(v : View)
    {
        viewModel.btnFollowFbPressed()
    }

    fun btnFollowTwitterClicked(v : View)
    {
        viewModel.btnFollowTwttrPressed()
    }

    fun btnFollowIgClicked(v : View)
    {
        viewModel.btnFollowIgPressed()
    }

    fun btnShareSMClicked(v : View)
    {
        viewModel.btnShareSMPressed()
    }

    fun setupAds() {
        val adRequest = AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build()
        try {
            adView.loadAd(adRequest)
        } catch (ex: Exception) {
            Log.e("Ads", ex.message)
        }
    }

    private fun setRingtone(context: Context, path: String?) {
        /*if (path == null) {
            return
        }
        val file = File(path)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        val filterName = path.substring(path.lastIndexOf("/") + 1)
        contentValues.put(MediaStore.MediaColumns.TITLE, filterName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
        contentValues.put(MediaStore.MediaColumns.SIZE, file.length())
        contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true)
        //val uri = MediaStore.Audio.Media.getContentUriForPath(path)

        Log.i("", "the absolute path of the file is :" + file.getAbsolutePath())
        val uri = MediaStore.Audio.Media.getContentUriForPath(
            file.getAbsolutePath()
        )
        val newUri = context.contentResolver.insert(uri, contentValues)
        Log.i("", "the ringtone uri is :$newUri")
        RingtoneManager.setActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_RINGTONE, newUri
        )
        */
    }
}
