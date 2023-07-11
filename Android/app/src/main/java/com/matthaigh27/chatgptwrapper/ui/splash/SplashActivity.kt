package com.matthaigh27.chatgptwrapper.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.base.BaseActivity
import com.matthaigh27.chatgptwrapper.ui.chat.view.ChatActivity
import com.matthaigh27.chatgptwrapper.ui.chat.view.dialogs.ConfirmDialog
import com.matthaigh27.chatgptwrapper.ui.chat.view.fragments.ChatMainFragment

class SplashActivity : BaseActivity() {

    private val PERMISSIONS_REQUEST_CODE = 1
    private lateinit var permissions: Array<String>
    private val CONFIRM_MESSAGE =
        "This app requires SMS, Contacts and Phone " +
                "permissions to function properly. " +
                "Please grant the necessary permissions."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestPermissions()
    }

    private fun requestPermissions() {
        /**
         * In mobile phones that use Google API 33 or higher, the permission for reading external storage
         * is disabled because the phones don't support the feature.
         */
        permissions = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE
            )
        } else {
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        val notGrantedPermissions = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            if (shouldShowRequestPermissionRationale(notGrantedPermissions[0])) {
                // show custom permission rationale
                val confirmDialog = ConfirmDialog(this@SplashActivity)
                confirmDialog.setOnClickListener(object :
                    ConfirmDialog.OnDialogButtonClickListener {
                    override fun onPositiveButtonClick() {
                        requestPermissions(
                            notGrantedPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE
                        )
                    }

                    override fun onNegativeButtonClick() {
                        finish()
                    }
                })

                confirmDialog.show()
                confirmDialog.setMessage(CONFIRM_MESSAGE)

            } else {
                requestPermissions(notGrantedPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            // Permissions already granted, navigate to your desired fragment
            moveToChatActivity()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissions granted, navigate to your desired fragment
                    moveToChatActivity()
                } else {
                    requestPermissions()
                }
                return
            }
        }
    }

    private fun moveToChatActivity() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, ChatActivity::class.java))
        }, 2000)
    }
}