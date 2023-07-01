package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.OnHideListener

class SendSmsWidget(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {

    private val context: Context

    private val edtPhoneNumber: TextInputLayout
    private val edtMessage: TextInputLayout

    var callback: ChatMessageInterface? = null
    var hideListener: OnHideListener? = null

    init {
        inflate(context, R.layout.widget_send_sms, this)
        this.context = context

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        edtPhoneNumber = findViewById(R.id.edt_phone_to_send)
        edtMessage = findViewById(R.id.edt_message)

        findViewById<ImageView>(R.id.btn_ok).setOnClickListener(this)
        findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)
    }

    fun setPhoneNumber(phonenumber: String) {
        edtPhoneNumber.editText?.setText(phonenumber)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_ok -> {
                if (edtPhoneNumber.editText?.text.toString().isEmpty() || edtMessage.editText?.text.toString().isEmpty()) {
                    Toast.makeText(
                        context, "Please input phone number and message.", Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                hideListener?.hide()
                callback?.sentSms(edtPhoneNumber.editText?.text.toString(), edtMessage.editText?.text.toString())
            }
            R.id.btn_cancel -> {
                hideListener?.hide()
                callback?.canceledSms()
            }
        }
    }
}