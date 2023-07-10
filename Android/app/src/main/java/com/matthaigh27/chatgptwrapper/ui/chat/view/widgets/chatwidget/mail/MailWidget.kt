package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.mail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.chat.MailModel
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface


class MailWidget(
    private val context: Context, private val mail: MailModel, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {

    var callback: ChatMessageInterface? = null

    private val txtTitle:TextView
    private val txtContent: TextView
    private val txtDate: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_mail, this, true)

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        txtTitle = findViewById(R.id.txt_title)
        txtContent = findViewById(R.id.txt_content)
        txtDate = findViewById(R.id.txt_date)

        txtTitle.text = mail.subject
        txtContent.text = mail.body
        txtDate.text = mail.date

        this.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        callback?.readMailInDetail(mail)
    }
}