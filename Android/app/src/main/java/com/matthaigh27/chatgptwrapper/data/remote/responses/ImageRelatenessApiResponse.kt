package com.matthaigh27.chatgptwrapper.data.remote.responses

data class ImageRelatenessApiResponse(
    val status_code: Int,
    val message: List<String>,
    val result: RelatenessResult
)

data class RelatenessResult(
    val program: String,
    val content: RelatenessContent
)

data class RelatenessContent (
    val image_name: String,
    val image_desc: String
)