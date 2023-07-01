package com.matthaigh27.chatgptwrapper.data.remote.requests.common


data class Keys(
    val uuid: String,
    val token: String,
    val openai_key: String,
    val pinecone_key: String,
    val pinecone_env: String,
    val firebase_key: String,
    val settings: OpenAISetting,
)
