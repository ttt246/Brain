package com.matthaigh27.chatgptwrapper.data.remote.responses

import com.google.gson.JsonElement

data class Result (
    val program: String,
    val content: JsonElement,
    val url: String
)