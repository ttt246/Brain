package com.matthaigh27.chatgptwrapper

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class RisingApplication : Application() {

    private var fcmToken: String = String()
    private var uuid: String = String()

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        initToken()
        // on below line we are getting device id.
        uuid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        appContext = applicationContext as RisingApplication
    }

    private fun initToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            fcmToken = task.result
        })
    }

    fun getFCMToken(): String {
        return this.fcmToken
    }

    fun getUUID(): String {
        return this.uuid
    }

    companion object {
        lateinit var appContext: RisingApplication
    }
}