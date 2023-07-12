package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.helpprompt

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.matthaigh27.chatgptwrapper.R

@SuppressLint("AppCompatCustomView")
class HelpPromptKeyItem(context: Context) : FrameLayout(context) {

    private val edtKey: TextInputLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.item_help_prompt_key, this, true)

        edtKey = findViewById(R.id.edt_key)
    }

    fun initView(keyName: String) {
        if(keyName.isNotEmpty())
            edtKey.hint = keyName.subSequence(1, keyName.length)
        tag = keyName
    }

    fun getText(): String {
        return edtKey.editText?.text.toString()
    }
}