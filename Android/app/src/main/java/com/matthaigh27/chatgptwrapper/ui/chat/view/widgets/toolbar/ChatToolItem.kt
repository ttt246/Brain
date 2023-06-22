package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.matthaigh27.chatgptwrapper.R

class ChatToolItem(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private lateinit var context: Context
    private lateinit var imgToolIcon: ImageView
    private lateinit var txtToolName: TextView
    private lateinit var clToolIcon: ConstraintLayout

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.item_chat_tool, this, true)

        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val itemMargin = context.resources.getDimensionPixelSize(R.dimen.spacing_small)
        layoutParams.setMargins(itemMargin, itemMargin, itemMargin, itemMargin)
        this.layoutParams = layoutParams

        this.context = context
        imgToolIcon = findViewById(R.id.img_chat_tool)
        clToolIcon = findViewById(R.id.cl_chat_tool)
        txtToolName = findViewById(R.id.txt_chat_tool)
    }

    fun setTool(drawableId: Int, size: Int, name: String) {
        imgToolIcon.setImageResource(drawableId)
        clToolIcon.layoutParams = LayoutParams(size, size)
        txtToolName.text = name
    }
}