package com.matthaigh27.chatgptwrapper.data.remote.requests

open class BaseRequest(
    val token: String,
    val uuid: String,
    val pineconeKey: String,
    val pineconeEnv: String,
    val firebaseKey: String,
    val setting: Setting
)

data class Setting(
    val temperature: String
)
