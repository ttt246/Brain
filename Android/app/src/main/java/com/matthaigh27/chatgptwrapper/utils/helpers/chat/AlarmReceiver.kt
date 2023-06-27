package com.matthaigh27.chatgptwrapper.utils.helpers.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val label = intent.getStringExtra("label")
        Log.d("AlarmReceiver", "Alarm triggered. Label: $label")
    }
}