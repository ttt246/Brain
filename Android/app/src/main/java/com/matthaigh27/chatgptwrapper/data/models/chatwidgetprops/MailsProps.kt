package com.matthaigh27.chatgptwrapper.data.models.chatwidgetprops

import com.google.gson.Gson
import com.matthaigh27.chatgptwrapper.data.models.chat.MailModel

data class MailsProps(
    val mails: ArrayList<MailModel>
) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun init(string: String): MailsProps {
            val gson = Gson()
            return gson.fromJson(string, MailsProps::class.java)
        }
    }
}