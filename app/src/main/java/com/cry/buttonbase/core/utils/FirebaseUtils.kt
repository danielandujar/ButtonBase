package com.cry.buttonbase.core.utils

import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.messaging.FirebaseMessaging
import java.io.File

class FirebaseUtils
{
    val mAuth = FirebaseAuth.getInstance()
    val mStorage = FirebaseStorage.getInstance()
    val mMessaging = FirebaseMessaging.getInstance()
    var isAuthenticated = false
    lateinit var app : Application

    init{
        //authUser()
    }

    fun authUser(app: Application, onSuccess : (Boolean)-> Unit){
        //
        this.app = app
        //
        if (mAuth.currentUser != null) {
            mAuth.signOut()
        }
        mAuth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                isAuthenticated = true
            }
            else {
                Log.e("FirebaseAuthentication", "Error authenticating user : " + it.result + " " + it.exception)
            }
            onSuccess(isAuthenticated)

        }
    }

    fun getFile( onComplete : (Boolean) -> Unit){
        // Create a storage reference from our app
        val storageRef = mStorage.reference
        val soundRef = storageRef.child("sound.mp3")
        val localFile = File(app.filesDir, "sound.mp3")

        soundRef.getFile(localFile)
            .addOnSuccessListener{
                // Local temp file has been created
                Log.d("DownloadingFile", "File Successfully Downloaded")
                onComplete(true)
                //
            }.addOnFailureListener {
                // Handle any errors
                onComplete(false)
            }

    }
}