package com.matthaigh27.chatgptwrapper.ui.chat.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.base.BaseActivity
import com.matthaigh27.chatgptwrapper.ui.chat.view.dialogs.ConfirmDialog
import com.matthaigh27.chatgptwrapper.ui.chat.view.fragments.ChatMainFragment


class ChatActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        navigateToChatMainFragment()
    }

    private fun navigateToChatMainFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_container, ChatMainFragment()).commit()
    }
}



