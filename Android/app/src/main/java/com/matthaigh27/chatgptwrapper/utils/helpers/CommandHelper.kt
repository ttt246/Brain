package com.matthaigh27.chatgptwrapper.utils.helpers

import com.google.gson.JsonArray
import com.matthaigh27.chatgptwrapper.data.models.HelpCommandModel
import com.matthaigh27.chatgptwrapper.data.models.HelpPromptModel
import com.matthaigh27.chatgptwrapper.utils.Constants.ERROR_MSG_JSON
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND_ALL
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND_ERROR_NO_INVALID_FORMAT
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND_ERROR_NO_MAIN
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object CommandHelper {
    private val FIELD_HELP_PROMPT_NAME = "name"
    private val FIELD_HELP_PROMPT_PROMPT = "prompt"
    private val FIELD_HELP_PROMPT_DESCRIPTION = "description"
    private val FIELD_HELP_PROMPT_TAGS = "tags"

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

    fun convertJsonArrayToHelpPromptList(promptData: String): ArrayList<HelpPromptModel> {
        val promptList = ArrayList<HelpPromptModel>()
        val promptJsonArray = JSONArray(promptData)

        try {
            for (i in 0 until promptJsonArray.length()) {
                val helpCommand = JSONObject(promptJsonArray[i].toString())

                val helpPromptModel = HelpPromptModel()
                helpPromptModel.name = helpCommand.getString(FIELD_HELP_PROMPT_NAME)
                helpPromptModel.description = helpCommand.getString(FIELD_HELP_PROMPT_DESCRIPTION)
                helpPromptModel.prompt = helpCommand.getString(FIELD_HELP_PROMPT_PROMPT)

                helpPromptModel.tags = ArrayList()
                val jsonArrayTags = helpCommand.getJSONArray(FIELD_HELP_PROMPT_TAGS)
                for (j in 0 until jsonArrayTags.length()) {
                    helpPromptModel.tags.add(jsonArrayTags[j].toString())
                }
                promptList.add(helpPromptModel)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            throw Exception(ERROR_MSG_JSON)
        }
        return promptList
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