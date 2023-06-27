package com.matthaigh27.chatgptwrapper.data.models.chat

import com.google.gson.Gson

data class HelpPromptModel(
    var name: String = "",
    var description: String = "",
    var prompt: String = "",
    var tags: ArrayList<String> = ArrayList()
) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun init(string: String): HelpPromptModel {
            val gson = Gson()
            return gson.fromJson(string, HelpPromptModel::class.java)
        }
    }
}
