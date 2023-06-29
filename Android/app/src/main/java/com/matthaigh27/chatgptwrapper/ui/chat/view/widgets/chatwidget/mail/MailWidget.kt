package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.mail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface


class MailWidget(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {

    private var context: Context
    var callback: ChatMessageInterface? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_mail, this, true)
        this.context = context

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        this.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

    }
}