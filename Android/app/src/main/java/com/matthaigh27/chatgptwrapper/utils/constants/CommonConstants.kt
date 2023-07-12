package com.matthaigh27.chatgptwrapper.utils.constants

import com.matthaigh27.chatgptwrapper.BuildConfig

object CommonConstants {
    val API_BASE_URL = BuildConfig.BASE_URL
    val FIREBASE_DATABASE_URL = "https://test3-83ffc-default-rtdb.firebaseio.com"

    val HELP_COMMAND_ERROR_NO_MAIN = "no main command"
    val HELP_COMMAND_ERROR_NO_INVALID_FORMAT = "Invalid Command Format"
    val HELP_COMMAND = "help"
    val HELP_COMMAND_ALL = "all"

    val ERROR_MSG_JSON = "Json Parsing Error"
    val ERROR_MSG_NOEXIST_COMMAND = "No such command name exists."
    val ERROR_MSG_UNKNOWN_ERROR = "Unknown Error happened"

    val PROPS_WIDGET_DESC = "widget description"
}