package com.matthaigh27.chatgptwrapper.utils.helpers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.matthaigh27.chatgptwrapper.data.models.AlarmModel

object AlarmHelper {
    fun getAlarmList(context: Context): List<AlarmModel> {
        val alarmsList = mutableListOf<AlarmModel>()
        val contentUri: Uri = Uri.parse("content://com.android.alarmclock/alarm")
        val cursor: Cursor? = context.contentResolver.query(contentUri, null, null, null, null)

        cursor?.let {
            val idIndex = it.getColumnIndex("_id")
            val timeIndex = it.getColumnIndex("alarmtime")
            val enabledIndex = it.getColumnIndex("enabled")
            val labelIndex = it.getColumnIndex("message")

            while (it.moveToNext()) {
                val id = it.getInt(idIndex)
                val time = it.getLong(timeIndex)
                val enabled = it.getInt(enabledIndex) == 1
                val label = it.getString(labelIndex)

                val alarm = AlarmModel(id, time, enabled, label)
                alarmsList.add(alarm)
            }
        }

        cursor?.close()
        return alarmsList
    }
}