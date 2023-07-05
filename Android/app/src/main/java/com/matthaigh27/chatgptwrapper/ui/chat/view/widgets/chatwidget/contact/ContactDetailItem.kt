package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.contact

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.VideoProfile
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface

class ContactDetailItem(
    private var context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtPhoneType: TextView
    private var phoneNumber: String = ""
    private var userName: String = ""

    private var visibilityListener: OnContactDetailVisibilityListener? = null
    var callback: ChatMessageInterface? = null

    init {
        initView()
    }

    fun setVisibilityListener(listener: OnContactDetailVisibilityListener) {
        visibilityListener = listener
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.item_contact_detail, this, true)

        txtPhoneNumber = findViewById(R.id.txt_phone_number)
        txtPhoneType = findViewById(R.id.txt_phone_type)

        findViewById<ImageView>(R.id.btn_voice_call).setOnClickListener(this)
        findViewById<ImageView>(R.id.btn_send_message).setOnClickListener(this)
    }

    fun setContactDetailItemInfo(phoneNumber: String, username: String) {
        this.phoneNumber = phoneNumber
        this.userName = username
        txtPhoneNumber.text = phoneNumber
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_voice_call -> {
                callback?.doVoiceCall(phoneNumber)
                doVoiceCall(phoneNumber)
            }

            R.id.btn_send_message -> {
                callback?.sendSmsWithPhoneNumber(phoneNumber)
            }
        }
        visibilityListener?.invisible()
    }

    private fun doVoiceCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(callIntent)
        return
    }

    fun openMessagingApp(phoneNumber: String, message: String) {
        val uri = Uri.parse("sms:$phoneNumber")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            putExtra("sms_body", message)
        }
        context.startActivity(intent)
    }

    private fun doVideoCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        callIntent.putExtra(
            "android.telecom.extra.START_CALL_WITH_VIDEO_STATE",
            VideoProfile.STATE_BIDIRECTIONAL
        )
        val hasCamera =
            context.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ?: false
        val hasTelephony =
            context.packageManager?.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ?: false
        val canMakeVideoCalls = hasCamera && hasTelephony

        if (canMakeVideoCalls) {
            context.startActivity(callIntent)
        }
    }

    interface OnContactDetailVisibilityListener {
        fun invisible()
    }
}