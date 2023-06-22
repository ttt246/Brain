package com.matthaigh27.chatgptwrapper.data.models

data class HelpCommandModel(
    var name: String = "",
    var description: String = "",
    var prompt: String = "",
    var tags: ArrayList<String> = ArrayList()
)
