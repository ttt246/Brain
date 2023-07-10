package com.matthaigh27.chatgptwrapper.data.remote.requests

import com.matthaigh27.chatgptwrapper.data.models.chat.ContactModel
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys

data class ReadMailApiRequest (
    val data: ReadMailData,
    val confs: Keys
)

data class ReadMailData (
    val sender: String,
    val pwd: String,
    val imap_folder: String,
)