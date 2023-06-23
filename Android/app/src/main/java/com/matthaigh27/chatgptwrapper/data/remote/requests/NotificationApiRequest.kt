package com.matthaigh27.chatgptwrapper.data.remote.requests

import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys

data class NotificationApiRequest(
    val message: String,
    val confs: Keys
)