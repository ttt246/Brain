package com.matthaigh27.chatgptwrapper.utils.helpers.chat

import com.matthaigh27.chatgptwrapper.data.models.chat.HelpCommandModel
import com.matthaigh27.chatgptwrapper.data.models.chat.HelpPromptModel
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND_ALL
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND_ERROR_NO_INVALID_FORMAT
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND_ERROR_NO_MAIN

object CommandHelper {
    fun isMainHelpCommand(model: HelpCommandModel): Boolean {
        return model.main != HELP_COMMAND
    }

    fun getHelpCommandFromStr(strCommand: String): HelpCommandModel {
        val commandModel = HelpCommandModel()
        if (strCommand == "/$HELP_COMMAND") {
            commandModel.main = HELP_COMMAND
            commandModel.assist = HELP_COMMAND_ALL
            return commandModel
        }
        try {
            if (strCommand.startsWith("/$HELP_COMMAND")) {
                val words = strCommand.split("\\s".toRegex()).toTypedArray()
                if (words.size != 2) {
                    throw Exception(HELP_COMMAND_ERROR_NO_MAIN)
                }
                commandModel.main = words[0].substring(1, words[0].length)
                commandModel.assist = words[1]
                if (commandModel.main!!.isEmpty() || commandModel.assist!!.isEmpty()) {
                    throw Exception(HELP_COMMAND_ERROR_NO_MAIN)
                }
            } else {
                commandModel.main = strCommand.substring(1, strCommand.length)
            }
        } catch (e: Exception) {
            throw Exception(HELP_COMMAND_ERROR_NO_INVALID_FORMAT)
        }
        return commandModel
    }

    fun makePromptUsage(list: ArrayList<HelpPromptModel>) : String {
        val usage = "usage:\n" +
                "- help command: /help [command name]\n" +
                "- prompt command: /<command name>\n\n"

        var strHelpList = "help prompt commands:"
        list.forEach { model ->
            strHelpList += "\n- " + model.name
        }
        return strHelpList
    }

    fun makePromptItemUsage(list: ArrayList<HelpPromptModel>, assistName: String): String {
        var strHelpDesc = ""
        list.forEach { model ->
            if (model.name == assistName) {
                var strTags = ""
                model.tags.forEach { tag ->
                    strTags += " $tag"
                }
                strHelpDesc = "description: " + model.description + "\ntags:" + strTags
            }
        }
        if (strHelpDesc.isEmpty())
            strHelpDesc = "No such command name exists."
        return strHelpDesc
    }
}