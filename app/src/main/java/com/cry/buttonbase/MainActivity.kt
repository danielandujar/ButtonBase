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
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import android.util.StatsLog.logEvent
import android.content.DialogInterface
import android.content.Intent
import android.content.ActivityNotFoundException
import android.app.Dialog
import android.net.Uri
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var viewModel : MainViewModel
    lateinit var buttonStateObserver : Observer<Boolean>

    val PREFS_NAME = "preferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        buttonStateObserver = Observer{
            btnSound.isActivated = it
        }
        viewModel.ButtonPressed.observe(this, buttonStateObserver)

        setupAds()

        setupRateDialog()
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

    fun setupAds()
    {
        val adRequest = AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build()
        try {
            adView.loadAd(adRequest)
        } catch (ex: Exception) {
            Log.e("Ads", ex.message)
        }
    }
}
