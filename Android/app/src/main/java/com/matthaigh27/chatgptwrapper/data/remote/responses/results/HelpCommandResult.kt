package com.matthaigh27.chatgptwrapper.data.remote.responses.results

data class HelpCommandResult(
    val program: String,
    val content: ArrayList<HelpCommandResponseItem>
)

data class HelpCommandResponseItem(
    val name: String,
    val description: String,
    val prompt: String,
    val tags: ArrayList<String>,
    val enabled: Boolean
)