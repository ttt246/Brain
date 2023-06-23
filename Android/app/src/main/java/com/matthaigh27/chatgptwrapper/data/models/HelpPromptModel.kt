package com.matthaigh27.chatgptwrapper.data.models

data class HelpPromptModel(
    var name: String = "",
    var description: String = "",
    var prompt: String = "",
    var tags: ArrayList<String> = ArrayList()
)
