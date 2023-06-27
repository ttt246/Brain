package com.matthaigh27.chatgptwrapper.data.models.chatwidgetprops

import com.google.gson.Gson
import com.matthaigh27.chatgptwrapper.data.models.chat.HelpPromptModel
import com.matthaigh27.chatgptwrapper.data.models.common.Time

data class ScheduleAlarmProps(
    val time: Time? = null,
    val label: String? = null,
    val repeat: BooleanArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleAlarmProps

        if (time != other.time) return false
        if (repeat != null) {
            if (other.repeat == null) return false
            if (!repeat.contentEquals(other.repeat)) return false
        } else if (other.repeat != null) return false
        if (label != other.label) return false

        return true
    }

    override fun hashCode(): Int {
        var result = time?.hashCode() ?: 0
        result = 31 * result + (repeat?.contentHashCode() ?: 0)
        result = 31 * result + (label?.hashCode() ?: 0)
        return result
    }

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
