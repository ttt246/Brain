package com.matthaigh27.chatgptwrapper.data.remote.requests

import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys

data class ComposeMailApiRequest(
    val data: ComposeMailData,
    val confs: Keys
)

data class ComposeMailData(
    private val sender: String,
    private val pwd: String,
    private val to: String,
    private val subject: String,
    private val body: String,
    private val to_send: Boolean,
    private val filename: String,
    private val file_content: String
)