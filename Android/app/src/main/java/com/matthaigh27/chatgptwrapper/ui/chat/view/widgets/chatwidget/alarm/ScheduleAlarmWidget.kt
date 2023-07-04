package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.alarm

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.common.Time
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.OnHideListener
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.AlarmHelper

class ScheduleAlarmWidget(
    context: Context,
    time: Time? = null,
    label: String? = null,
    repeat: BooleanArray? = null,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {

    private val WEEK_DAY_COUNT = 7
    private val dayOfWeek = arrayOf(
        "S", "M", "T", "W", "T", "F", "S"
    )
    private val selectedDayList: BooleanArray = BooleanArray(WEEK_DAY_COUNT) { false }
    private val dayItemList: ArrayList<DayOfWeekItem> = ArrayList()

    private val context: Context
    private var repeat: BooleanArray? = null
    private var llRpeat: LinearLayout
    private var txtLabel: TextInputLayout
    private var timePicker: TimePicker

    var callback: ChatMessageInterface? = null
    var hideListener: OnHideListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_schedule_alarm, this, true)
        this.context = context
        this.repeat = repeat

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        findViewById<View>(R.id.btn_ok).setOnClickListener(this)
        findViewById<View>(R.id.btn_cancel).setOnClickListener(this)

        llRpeat = findViewById(R.id.ll_repeat)
        txtLabel = findViewById(R.id.txt_label)
        if (label != null) {
            txtLabel.editText!!.setText(label)
        }
        timePicker = findViewById(R.id.time_picker)
        if (time != null) {
            timePicker.hour = time.hour
            timePicker.minute = time.minute
        }

        initRepeatSetting()
    }

    private fun initRepeatSetting() {
        dayOfWeek.forEachIndexed { index, day ->
            val item = DayOfWeekItem(context, day)
            item.setOnClickListener {
                selectedDayList[index] = !selectedDayList[index]
            }
            dayItemList.add(item)
            llRpeat.addView(item)
        }
        if (repeat != null) {
            setSelectedDay(repeat!!)
        }
    }

    private fun setSelectedDay(list: BooleanArray) {
        list.forEachIndexed { index, status ->
            dayItemList[index].isChecked = status
            dayItemList[index].updateBackground()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_ok -> {
                AlarmHelper.createAlarm(
                    context,
                    timePicker.hour,
                    timePicker.minute,
                    txtLabel.editText?.text.toString()
                )
                callback?.setAlarm(
                    timePicker.hour,
                    timePicker.minute,
                    txtLabel.editText?.text.toString()
                )
            }

            R.id.btn_cancel -> {
                callback?.cancelAlarm()
            }
        }
        hideListener?.hide()
    }
}