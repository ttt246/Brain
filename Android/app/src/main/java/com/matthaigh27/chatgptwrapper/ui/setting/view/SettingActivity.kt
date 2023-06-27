package com.matthaigh27.chatgptwrapper.ui.setting.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.google.android.material.textfield.TextInputLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.ui.base.BaseActivity
import com.matthaigh27.chatgptwrapper.ui.chat.view.ChatActivity

class SettingActivity : BaseActivity() {

    private lateinit var txtPineconeKey: TextInputLayout
    private lateinit var txtPineconeEnv: TextInputLayout
    private lateinit var txtFirebaseKey: TextInputLayout
    private lateinit var txtTemperature: TextInputLayout
    private lateinit var txtUUID: TextInputLayout
    private lateinit var txtOpenAIKey: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        init()
    }

    private fun init() {
        txtPineconeKey = findViewById(R.id.txt_pinecone_key)
        txtPineconeEnv = findViewById(R.id.txt_pinecone_env)
        txtFirebaseKey = findViewById(R.id.txt_firebase_key)
        txtTemperature = findViewById(R.id.txt_temperature)
        txtUUID = findViewById(R.id.txt_uuid)
        txtOpenAIKey = findViewById(R.id.txt_openai_key)

        findViewById<View>(R.id.btn_setting_save).setOnClickListener { saveSettingData() }
        findViewById<View>(R.id.btn_back_chat).setOnClickListener { backToChatMain() }

        val uuid = appContext.getUUID()
        txtUUID.editText?.setText(uuid)
    }

    private fun saveSettingData() {
        val pineconeKey = txtPineconeKey.editText?.text.toString()
        val pineconeEnv = txtPineconeEnv.editText?.text.toString()
        val firebaseKey = txtFirebaseKey.editText?.text.toString()
        val temperature = txtTemperature.editText?.text.toString()
        val openai = txtTemperature.editText?.text.toString()
    }

    private fun backToChatMain() {
        startActivity(Intent(this@SettingActivity, ChatActivity::class.java))
    }
}