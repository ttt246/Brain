package com.matthaigh27.chatgptwrapper.data.models.setting

import com.google.gson.Gson
import com.matthaigh27.chatgptwrapper.data.models.chat.HelpPromptModel
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.OpenAISetting

data class SettingModel(
    val serverUrl: String,
    val openaiKey: String,
    val pineconeEnv: String,
    val pineconeKey: String,
    val firebaseKey: String,
    val setting: OpenAISetting
) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun init(string: String): SettingModel {
            val gson = Gson()
            return gson.fromJson(string, SettingModel::class.java)
        }
    }
}
