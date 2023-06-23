package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.helpprompt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.HelpPromptModel
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.OnHideListener

class HelpPromptWidget(context: Context, model: HelpPromptModel) : ConstraintLayout(context),
    View.OnClickListener {
    private lateinit var llPromptKeys: LinearLayout
    private lateinit var txtKeysTitle: TextView

    private var promptEditTextList: ArrayList<EditText>? = null
    private val promptModel: HelpPromptModel
    var callback: ChatMessageInterface? = null
    var hideListener: OnHideListener? = null

    init {
        promptModel = model
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.widget_help_prompt, this, true)

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        llPromptKeys = findViewById(R.id.ll_prompt_keys)
        txtKeysTitle = findViewById(R.id.txt_keys_title)
        txtKeysTitle.text = promptModel.name

        findViewById<Button>(R.id.btn_ok).setOnClickListener(this)
        findViewById<Button>(R.id.btn_cancel).setOnClickListener(this)

        initPromptList()

    }

    private fun initPromptList() {
        if (promptModel.tags.size > 0) {
            llPromptKeys.removeAllViews()
            promptEditTextList = ArrayList()

            for (i in 0 until promptModel.tags.size) {
                val edtKey = HelpPromptKeyEditText(context)
                edtKey.initView(promptModel.tags[i])
                llPromptKeys.addView(edtKey)
                promptEditTextList?.add(edtKey)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_ok -> {
                outputCompletePrompt()
            }
            R.id.btn_cancel -> {
                callback?.canceledHelpPrompt()
            }
        }
        hideListener?.hide()
    }

    private fun outputCompletePrompt() {
        var promptTemplate = promptModel.prompt
        if(promptModel.tags.size > 0) {
            promptEditTextList?.forEach { edtKeyPrompt ->
                val prompt = edtKeyPrompt.text.toString()
                if(prompt.isEmpty()) return
                promptTemplate = promptTemplate.replace(edtKeyPrompt.tag.toString(), prompt)
            }
        }
        callback?.sentHelpPrompt(promptTemplate)
    }
}