package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.mail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.OnHideListener

class ReadMailWidget(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {

    private var context: Context
    var callback: ChatMessageInterface? = null

    private var edtMailFrom: TextInputLayout
    private var edtMailPassword: TextInputLayout
    private var swhMailType: SwitchMaterial

    var hideListener: OnHideListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_mail_read, this, true)
        this.context = context

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        this.setOnClickListener(this)
        findViewById<ImageView>(R.id.btn_send).setOnClickListener(this)
        findViewById<ImageView>(R.id.btn_send_cancel).setOnClickListener(this)

        edtMailFrom = findViewById(R.id.edt_mail_from)
        edtMailPassword = findViewById(R.id.edt_mail_password)
        swhMailType = findViewById(R.id.swh_mail_type)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_send -> {
                val imapFolder = if (swhMailType.isChecked) "inbox" else "draft"
                callback?.readMail(
                    from = edtMailFrom.editText?.text.toString(),
                    password = edtMailPassword.editText?.text.toString(),
                    imap_folder = imapFolder
                )
            }

            R.id.btn_cancel -> {
                hideListener?.hide()
            }
        }
    }
}