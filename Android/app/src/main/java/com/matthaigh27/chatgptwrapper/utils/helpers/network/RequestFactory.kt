package com.matthaigh27.chatgptwrapper.utils.helpers.network

import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.remote.requests.BaseApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Settings

object RequestFactory {

    fun buildApiKeys(): Keys {
        return Keys(
            uuid = appContext.getUUID(),
            token = appContext.getFCMToken(),
            openai_key = "",
            pinecone_key = "",
            pinecone_env = "",
            firebase_key = "",
            settings = Settings(0.6f)
        )
    }

    fun buildBaseApiRequest(): BaseApiRequest {
        return BaseApiRequest(
            confs = buildApiKeys()
        )
    }
}