package com.matthaigh27.chatgptwrapper.data.remote.requests

import com.matthaigh27.chatgptwrapper.data.models.ContactModel
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys

data class TrainImageApiRequest(
    val image_name: String,
    val status: String,
    val confs: Keys
)
