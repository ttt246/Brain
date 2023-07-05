package com.matthaigh27.chatgptwrapper.data.remote.responses

data class ApiResponse <T> (
    val status_code: Int,
    val message: List<String>,
    val result: T
)
