package com.matthaigh27.chatgptwrapper.data.models

import com.google.gson.JsonElement

data class ChatMessageModel(
    val type: Int,
    val content: String? = null,
    val data: JsonElement? = null,
    val hasImage: Boolean = false,
    val image: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessageModel

        if (type != other.type) return false
        if (content != other.content) return false
        if (data != other.data) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + content.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}
