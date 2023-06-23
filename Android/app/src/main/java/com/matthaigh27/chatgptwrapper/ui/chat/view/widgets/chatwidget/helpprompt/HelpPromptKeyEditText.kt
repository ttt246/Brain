package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.helpprompt

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.Dimension
import com.matthaigh27.chatgptwrapper.R

@SuppressLint("AppCompatCustomView")
class HelpPromptKeyEditText(context: Context) : EditText(context) {
    init {
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val marginBottom =
            context.resources.getDimensionPixelSize(R.dimen.spacing_tiny)
        layoutParams.setMargins(0, 0, 0, marginBottom)
        this.layoutParams = layoutParams

        minHeight =
            context.resources.getDimensionPixelSize(R.dimen.height_edittext_normal)

        val paddingStart =
            context.resources.getDimensionPixelSize(R.dimen.spacing_tiny)
        setPadding(paddingStart, 0, 0, 0)


        setTextSize(
            Dimension.DP,
            context.resources.getDimensionPixelSize(R.dimen.font_normal)
                .toFloat()
        )
        setTextColor(context.getColor(R.color.color_accent))
        setHintTextColor(context.getColor(R.color.color_primary_dark))
        background = context.getDrawable(R.drawable.bg_edittext_radius_small)
    }

    fun initView(keyName: String) {
        hint = keyName
        tag = keyName
    }
}