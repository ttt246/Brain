package com.matthaigh27.chatgptwrapper.models.viewmodels

import androidx.lifecycle.ViewModel

/**
 * ChatModel for Chat RecyclerView
 */
class ChatMessageModel: ViewModel() {
    var message: String = ""
    var isMe: Boolean = true
    var image: ByteArray? = null
    var imageName: String = ""
    var visibleFeedback = false
    var feedback = 0
    var isWidget: Boolean = false
    var widgetType = ""
    var widgetDescription = ""
}
