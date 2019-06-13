package com.cry.buttonbase.core.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.ActivityNotFoundException
import android.net.Uri

class SocialMediaManager
{
    fun getFacebookIntent(pm: PackageManager, url: String): Intent {
        var uri = Uri.parse(url)
        try {
            if (pm.getApplicationInfo("com.facebook.katana", 0).enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=$url")
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return Intent("android.intent.action.VIEW", uri)
    }

    fun getTwitterIntent(pm: PackageManager, twitterName: String): Intent {
        try {
            return Intent("android.intent.action.VIEW", Uri.parse("twitter://user?screen_name=$twitterName"))
        } catch (e: ActivityNotFoundException) {
            return Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/#!/$twitterName"))
        }

    }

    fun getInstagramIntent(pm: PackageManager, instagramName: String): Intent {
        val likeIng = Intent("android.intent.action.VIEW", Uri.parse("http://instagram.com/_u/$instagramName"))
        try {
            val appInfo = pm.getApplicationInfo("com.instagram.android", 0)
            likeIng.setPackage("com.instagram.android")
            return likeIng
        } catch (e: PackageManager.NameNotFoundException) {
            return Intent("android.intent.action.VIEW", Uri.parse("http://instagram.com/$instagramName"))
        }

    }
    fun openFacebook(context : Context)
    {
        context.startActivity(getFacebookIntent(context.packageManager, "https://www.facebook.com/andujardev"))
    }
    fun openInstagram(context : Context)
    {
        context.startActivity(getInstagramIntent(context.packageManager, "andujardev"))
    }
    fun openTwitter(context : Context)
    {
        context.startActivity(getTwitterIntent(context.packageManager, "andujardev"))
    }
    fun shareSocial(context : Context)
    {
        val sendIntent = Intent()
        sendIntent.action = "android.intent.action.SEND"
        sendIntent.putExtra(
            "android.intent.extra.TEXT",
            "CHECK OUT THIS APP!!: https://play.google.com/store/apps/details?id=" + context.packageName
        )
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }
}