package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface

class SendSmsWidget(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val context: Context

    private val edtPhoneNumber: EditText
    private val edtMessage: EditText

    private val btnOk: Button
    private val btnCancel: Button

    var callback: ChatMessageInterface? = null

    init {
        inflate(context, R.layout.widget_send_sms, this)
        this.context = context

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        edtPhoneNumber = findViewById(R.id.edt_phone_to_send)
        edtMessage = findViewById(R.id.edt_message)

        btnOk = findViewById(R.id.btn_ok)
        btnCancel = findViewById(R.id.btn_cancel)

        btnOk.setOnClickListener {
            if (edtPhoneNumber.text.toString().isEmpty() || edtMessage.text.toString().isEmpty()) {
                Toast.makeText(
                    context, "Please input phone number and message.", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            callback?.sentSms(edtPhoneNumber.text.toString(), edtMessage.text.toString())
        }

        btnCancel.setOnClickListener {
            callback?.canceledSms()
        }
    }

    fun setPhoneNumber(phonenumber: String) {
        edtPhoneNumber.setText(phonenumber)
    }
}