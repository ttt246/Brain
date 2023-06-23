package com.matthaigh27.chatgptwrapper.ui.chat.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.base.BaseActivity
import com.matthaigh27.chatgptwrapper.ui.chat.view.dialogs.ConfirmDialog
import com.matthaigh27.chatgptwrapper.ui.chat.view.fragments.ChatMainFragment


class ChatActivity : BaseActivity() {

    private val PERMISSIONS_REQUEST_CODE = 1
    private val PERMISSIONS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val CONFIRM_MESSAGE =
        "This app requires SMS, Contacts and Phone " +
                "permissions to function properly. " +
                "Please grant the necessary permissions."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        requestPermissions()
    }

    private fun requestPermissions() {
        val notGrantedPermissions = PERMISSIONS.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            if (shouldShowRequestPermissionRationale(notGrantedPermissions[0])) {
                // show custom permission rationale
                val confirmDialog = ConfirmDialog(this@ChatActivity)
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
            navigateToChatMainFragment()
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
                    navigateToChatMainFragment()
                } else {
                    requestPermissions()
                }
                return
            }
        }
    }

    private fun navigateToChatMainFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_container, ChatMainFragment()).commit()
    }
}



