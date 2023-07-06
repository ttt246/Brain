package com.matthaigh27.chatgptwrapper.data.models.chat

import javax.annotation.ParametersAreNullableByDefault

data class AutoTaskModel(
    val command: Command? = null,
    val result: String? = null,
    val thoughts: Thoughts? = null
)

data class Command(
    val args: Args? = null,
    val name: String? = null
)

data class Args(
    val tool_input: String? = null,
    val file_path: String? = null,
    val text: String? = null,
    val response: String? = null
)

data class Thoughts(
    val criticism: String? = null,
    val plan: String? = null,
    val reasoning: String? = null,
    val speak: String? = null,
    val text: String? = null
)