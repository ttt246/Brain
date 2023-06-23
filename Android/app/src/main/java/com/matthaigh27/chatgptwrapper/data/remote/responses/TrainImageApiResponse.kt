package com.matthaigh27.chatgptwrapper.data.remote.responses

data class TrainImageApiResponse(
    val status_code: Int,
    val message: List<String>,
    val result: ImageInfo
)

data class ImageInfo(
    val image_name: String,
    val image_text: String
)