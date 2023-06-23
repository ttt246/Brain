package com.matthaigh27.chatgptwrapper.utils.helpers

import com.matthaigh27.chatgptwrapper.data.models.HelpPromptModel
import com.matthaigh27.chatgptwrapper.utils.Constants
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Converter {
    fun stringToHelpPromptList(promptData: String): ArrayList<HelpPromptModel> {
        val promptList = ArrayList<HelpPromptModel>()
        val promptJsonArray = JSONArray(promptData)

        try {
            for (i in 0 until promptJsonArray.length()) {
                val helpCommand = JSONObject(promptJsonArray[i].toString())

                val helpPromptModel = HelpPromptModel()
                helpPromptModel.name = helpCommand.getString(Constants.FIELD_HELP_PROMPT_NAME)
                helpPromptModel.description = helpCommand.getString(Constants.FIELD_HELP_PROMPT_DESCRIPTION)
                helpPromptModel.prompt = helpCommand.getString(Constants.FIELD_HELP_PROMPT_PROMPT)

                helpPromptModel.tags = ArrayList()
                val jsonArrayTags = helpCommand.getJSONArray(Constants.FIELD_HELP_PROMPT_TAGS)
                for (j in 0 until jsonArrayTags.length()) {
                    helpPromptModel.tags.add(jsonArrayTags[j].toString())
                }
                promptList.add(helpPromptModel)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            throw Exception(Constants.ERROR_MSG_JSON)
        }
        return promptList
    }
}