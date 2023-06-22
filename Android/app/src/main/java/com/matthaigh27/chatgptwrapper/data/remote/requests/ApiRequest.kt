package com.matthaigh27.chatgptwrapper.data.remote.requests

data class ApiRequest(
    val token: String,
    val uuid: String,
    val model: String,
    val message: String
)