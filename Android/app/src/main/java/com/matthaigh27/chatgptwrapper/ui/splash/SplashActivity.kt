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

/**
 * An Activity class for splash screen
 *
 * This class not only shows splash screen, but send permission requests to users.
 */
class SplashActivity : BaseActivity() {

    private val PERMISSIONS_REQUEST_CODE = 1
    private val SPLASH_DELAY_TIME = 3500L
    /**
     * This is string array variable to store a list of permissions to send to users.
     */
    private lateinit var permissions: Array<String>
    /**
     * This is string variable to present warning when users didn't accept all given permissions.
     */
    private lateinit var confirmMessage: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        requestPermissions()
        confirmMessage = getString(R.string.message_confirm_permission)
    }

    /**
     * Send permission requests to users.
     *
     * This function is used to send permission requests to users for the normal operation of our app.
     * If you refuse even one request, confirm dialog that recommend users to accept the refused requests again
     * are showed.
     */
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
            /**
             * If some permissions that users didn't accept exist, this code block are executed.
             */
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
                confirmDialog.setMessage(confirmMessage)

            } else {
                requestPermissions(notGrantedPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            /**
             * Permissions already granted, navigate to your desired activity
             */
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
                    /**
                     * Permissions granted, navigate to your desired activity
                     */
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
        }, SPLASH_DELAY_TIME)
    }
}