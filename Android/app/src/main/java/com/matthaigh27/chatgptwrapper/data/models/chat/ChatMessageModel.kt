package com.matthaigh27.chatgptwrapper.data.models.chat

import com.google.gson.JsonElement

data class ChatMessageModel(
    val type: Int,
    val content: String? = null,
    val data: JsonElement? = null,
    val hasImage: Boolean = false,
    val image: ByteArray? = null,
)
