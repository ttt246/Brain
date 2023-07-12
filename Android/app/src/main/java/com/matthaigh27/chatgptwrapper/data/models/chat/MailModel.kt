package com.matthaigh27.chatgptwrapper.data.models.chat

data class MailModel(
    val from: String,
    val to: String,
    val date: String,
    val cc: String,
    val subject: String,
    val body: String,
)
