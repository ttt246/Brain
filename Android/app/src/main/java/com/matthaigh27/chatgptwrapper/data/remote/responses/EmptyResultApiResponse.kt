package com.matthaigh27.chatgptwrapper.data.remote.responses

data class EmptyResultApiResponse(
    val status_code: Int,
    val message: List<String>,
    val result: String
)