package com.matthaigh27.chatgptwrapper.utils.helpers.chat

import com.matthaigh27.chatgptwrapper.data.models.setting.SettingModel
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.OpenAISetting
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.API_BASE_URL

object SettingHelper {
    fun emptySettingModel(): SettingModel{
        return SettingModel(
            serverUrl = API_BASE_URL,
            openaiKey = "",
            pineconeEnv = "",
            pineconeKey = "",
            firebaseKey = "",
            setting = OpenAISetting(0f)
        )
    }
}