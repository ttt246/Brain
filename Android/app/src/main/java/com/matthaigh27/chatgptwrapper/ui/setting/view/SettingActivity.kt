package com.matthaigh27.chatgptwrapper.ui.setting.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.models.setting.SettingModel
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.OpenAISetting
import com.matthaigh27.chatgptwrapper.ui.base.BaseActivity
import com.matthaigh27.chatgptwrapper.ui.chat.view.ChatActivity
import com.matthaigh27.chatgptwrapper.ui.chat.view.dialogs.ConfirmDialog
import com.matthaigh27.chatgptwrapper.ui.setting.viewmodel.SettingViewModel

class SettingActivity : BaseActivity() {
    private val CONFIRM_MESSAGE = "Are you sure you want to set?"

    private lateinit var txtPineconeKey: TextInputLayout
    private lateinit var txtPineconeEnv: TextInputLayout
    private lateinit var txtFirebaseKey: TextInputLayout
    private lateinit var txtTemperature: TextInputLayout
    private lateinit var txtUUID: TextInputLayout
    private lateinit var txtOpenAIKey: TextInputLayout

    private lateinit var viewModel: SettingViewModel

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

        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]

        initSettingData()
    }

    private fun initSettingData() {
        viewModel.getSettingData().observe(this, Observer { resource ->
            when (resource) {
                is ApiResource.Loading -> {
                }

                is ApiResource.Success -> {
                    resource.data?.let { data ->
                        txtPineconeKey.editText?.setText(data.pineconeKey)
                        txtPineconeEnv.editText?.setText(data.pineconeEnv)
                        txtFirebaseKey.editText?.setText(data.firebaseKey)
                        txtTemperature.editText?.setText(data.setting.temperature.toString())
                        txtOpenAIKey.editText?.setText(data.openaiKey)
                    }
                }

                is ApiResource.Error -> {
                    showToast("No Data")
                }
            }
        })
    }

    private fun updateSettingData(model: SettingModel) {
        viewModel.setSettingData(model).observe(this, Observer { resource ->
            when (resource) {
                is ApiResource.Loading -> {
                }

                is ApiResource.Success -> {
                    showToast("Success to save your setting!")
                }

                is ApiResource.Error -> {
                    showToast("No Data")
                }
            }
        })
    }

    private fun saveSettingData() {
        val pineconeKey = txtPineconeKey.editText?.text.toString()
        val pineconeEnv = txtPineconeEnv.editText?.text.toString()
        val firebaseKey = txtFirebaseKey.editText?.text.toString()
        val temperature = txtTemperature.editText?.text.toString().toFloat()
        val openaiKey = txtOpenAIKey.editText?.text.toString()

        if (pineconeEnv == "" || pineconeKey == "" || firebaseKey == "" || openaiKey == ""){
            showToast("Please fill out all the inputs")
            return
        }


        val confirmDialog = ConfirmDialog(this)
        confirmDialog.setOnClickListener(object : ConfirmDialog.OnDialogButtonClickListener {
            override fun onPositiveButtonClick() {
                val setting = SettingModel(
                    openaiKey = openaiKey,
                    pineconeEnv = pineconeEnv,
                    pineconeKey = pineconeKey,
                    firebaseKey = firebaseKey,
                    setting = OpenAISetting(
                        temperature
                    )
                )
                updateSettingData(setting)
            }

            override fun onNegativeButtonClick() {
            }
        })

        confirmDialog.show()
        confirmDialog.setMessage(CONFIRM_MESSAGE)
    }

    private fun backToChatMain() {
        startActivity(Intent(this@SettingActivity, ChatActivity::class.java))
    }
}