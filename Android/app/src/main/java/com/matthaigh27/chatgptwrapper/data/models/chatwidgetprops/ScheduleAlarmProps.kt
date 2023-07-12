package com.matthaigh27.chatgptwrapper.data.models.chatwidgetprops

import com.google.gson.Gson
import com.matthaigh27.chatgptwrapper.data.models.chat.HelpPromptModel
import com.matthaigh27.chatgptwrapper.data.models.common.Time

data class ScheduleAlarmProps(
    val time: Time? = null,
    val label: String? = null,
    val repeat: BooleanArray? = null
) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun init(string: String): ScheduleAlarmProps {
            val gson = Gson()
            return gson.fromJson(string, ScheduleAlarmProps::class.java)
        }
    }
}
