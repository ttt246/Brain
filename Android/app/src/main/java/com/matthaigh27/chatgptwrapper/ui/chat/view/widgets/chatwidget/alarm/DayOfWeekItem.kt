package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.alarm

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.matthaigh27.chatgptwrapper.R

class DayOfWeekItem(
    context: Context, day: String, attrs: AttributeSet? = null
) : LinearLayout(context, attrs), View.OnClickListener {

    private val context: Context
    private var txtDay: TextView
    var isChecked = false

    init {
        inflate(context, R.layout.item_day_of_week, this)
        this.context = context

        layoutParams = LayoutParams(
        0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )

        val padding = context.resources.getDimensionPixelSize(R.dimen.spacing_tiny)

        txtDay = findViewById(R.id.txt_day)
        txtDay.text = day

        txtDay.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_day -> {
                isChecked = !isChecked
                updateBackground()
            }
        }
    }

    fun updateBackground() {
        if (isChecked) {
            txtDay.background = resources.getDrawable(R.drawable.bg_circle_button_schedule_alarm_day_selected)
        } else {
            txtDay.background = resources.getDrawable(R.drawable.bg_circle_button_schedule_alarm_day)
        }
    }
}