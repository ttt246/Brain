package com.matthaigh27.chatgptwrapper.data.models

data class ChatMessageModel(
    var type: Int,
    var content: String,
    var data: String? = null
)
