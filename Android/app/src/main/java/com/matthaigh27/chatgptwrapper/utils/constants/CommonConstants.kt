package com.matthaigh27.chatgptwrapper.utils.constants

import com.matthaigh27.chatgptwrapper.BuildConfig

object CommonConstants {
    val API_BASE_URL = BuildConfig.BASE_URL

    val HELP_COMMAND_ERROR_NO_MAIN = "no main command"
    val HELP_COMMAND_ERROR_NO_INVALID_FORMAT = "Invalid Command Format"
    val HELP_COMMAND = "help"
    val HELP_COMMAND_ALL = "all"

    val ERROR_MSG_JSON = "Json Parsing Error"
    val ERROR_MSG_NOEXIST_COMMAND = "No such command name exists."

    val MSG_HELP_RPOMPT = "Help Prompt Command"

    val FIELD_HELP_PROMPT_NAME = "name"
    val FIELD_HELP_PROMPT_PROMPT = "prompt"
    val FIELD_HELP_PROMPT_DESCRIPTION = "description"
    val FIELD_HELP_PROMPT_TAGS = "tags"

    val PROPS_WIDGET_DESC = "widget description"

    val TIME_OUT_CALL = 60L
    val TIME_OUT_CONNECT = 60L
    val TIME_OUT_READ = 60L
    val TIME_OUT_WRITE = 60L
}