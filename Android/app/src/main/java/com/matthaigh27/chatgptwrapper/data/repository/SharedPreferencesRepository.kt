package com.matthaigh27.chatgptwrapper.data.repository

import android.content.Context
import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.models.setting.SettingModel
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.SettingHelper.emptySettingModel

object SharedPreferencesRepository {
    fun saveConfig(config: SettingModel) {
        val sharedPreferences = appContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val jsonString = config.toString()
        editor.putString("config", jsonString)
        editor.apply()
    }

    fun getConfig(): SettingModel {
        val sharedPreferences = appContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("config", "")
        if (jsonString == "" || jsonString == null) {
            return emptySettingModel()
        } else {
            return SettingModel.init(jsonString)
        }
    }
}