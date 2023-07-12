package com.matthaigh27.chatgptwrapper.utils.helpers.chat

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.provider.AlarmClock
import android.util.Log
import java.util.Calendar


object AlarmHelper {

    /**
     * This function is used to set an alarm with time and label
     */
    fun createAlarm(context: Context, hour: Int, minute: Int, label: String) {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)


        val intent = Intent("android.intent.action.SET_ALARM")
        intent.putExtra("android.intent.extra.alarm.HOUR", hour)
        intent.putExtra("android.intent.extra.alarm.MINUTES", minute)
        intent.putExtra("android.intent.extra.alarm.SKIP_UI", true)
        intent.putExtra("android.intent.extra.alarm.MESSAGE", label)

        context.startActivity(intent)
    }

    fun scheduleRepeatingAlarm(context: Context, selectedDays: ArrayList<Int>, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.action = "com.matthaigh27.chatgptwrapper"

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Calculate the time difference between the next alarm time and now.
        val calendar = Calendar.getInstance()
        val timeNow = calendar.timeInMillis
        var minTimeDiff = Long.MAX_VALUE

        for (dayOfWeek in selectedDays) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

            var alarmTime = calendar.timeInMillis
            if (alarmTime < timeNow) { // If the alarm time is in the past, set it for the next week.
                alarmTime += AlarmManager.INTERVAL_DAY * 7
            }

            val timeDiff = alarmTime - timeNow
            if (timeDiff < minTimeDiff) {
                minTimeDiff = timeDiff
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            timeNow + minTimeDiff,
            AlarmManager.INTERVAL_DAY * 7, // Set to repeat every week.
            pendingIntent
        )
    }
}
