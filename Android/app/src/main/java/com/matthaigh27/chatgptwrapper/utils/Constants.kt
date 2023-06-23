package com.matthaigh27.chatgptwrapper.utils

object Constants {
    val API_BASE_URL = "https://ttt246-brain.hf.space/"

    val TYPE_RESPONSE_MESSAGE = "message"
    val TYPE_RESPONSE_BROWSER = "browser"
    val TYPE_RESPONSE_ALERT = "alert"
    val TYPE_RESPONSE_URL = "url"
    val TYPE_RESPONSE_IMAGE = "image"
    val TYPE_RESPONSE_HELP_COMMAND = "help_command"
    val TYPE_RESPONSE_SMS = "sms"
    val TYPE_RESPONSE_CONTACT = "contact"

    val TYPE_WIDGET_SMS = "sms"
    val TYPE_WIDGET_HELP_PROMPT = "help_prompt"
    val TYPE_WIDGET_FEEDBACK = "feedback"
    val TYPE_WIDGET_SEARCH_CONTACT = "search_contact"

    val HELP_COMMAND_ERROR_NO_MAIN = "no main command"
    val HELP_COMMAND_ERROR_NO_INVALID_FORMAT = "Invalid Command Format"
    val HELP_COMMAND = "help"
    val HELP_COMMAND_ALL = "all"

    val ERROR_MSG_JSON = "Json Parsing Error"
    val ERROR_MSG_NOEXIST_COMMAND = "No such command name exists."

    val MSG_HELP_RPOMPT = "Help Prompt Command"
}