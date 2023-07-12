package com.matthaigh27.chatgptwrapper.data.remote.responses.results

import com.google.gson.JsonElement

data class CommonResult(
    val program: String,
    val content: JsonElement
)