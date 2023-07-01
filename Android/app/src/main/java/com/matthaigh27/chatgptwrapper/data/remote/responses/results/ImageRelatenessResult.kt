package com.matthaigh27.chatgptwrapper.data.remote.responses.results

data class ImageRelatenessResult(
    val program: String,
    val content: ImageRelatenessContent
)

data class ImageRelatenessContent (
    val image_name: String,
    val image_desc: String
)