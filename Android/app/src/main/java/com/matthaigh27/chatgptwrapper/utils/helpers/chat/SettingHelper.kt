package com.matthaigh27.chatgptwrapper.utils.helpers.chat

import com.matthaigh27.chatgptwrapper.data.models.setting.SettingModel
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.OpenAISetting

object SettingHelper {
    fun emptySettingModel(): SettingModel{
        return SettingModel(
            openaiKey = "",
            pineconeEnv = "",
            pineconeKey = "",
            firebaseKey = "",
            setting = OpenAISetting(0f)
        )
    }
}