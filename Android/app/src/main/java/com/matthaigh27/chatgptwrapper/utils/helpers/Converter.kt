package com.matthaigh27.chatgptwrapper.utils.helpers

import com.matthaigh27.chatgptwrapper.data.models.chat.HelpPromptModel
import com.matthaigh27.chatgptwrapper.data.models.common.Time
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.HelpCommandResponseItem
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Converter {
    fun responseToHelpPromptList(promptData: ArrayList<HelpCommandResponseItem>): ArrayList<HelpPromptModel> {
        val promptList = ArrayList<HelpPromptModel>()

        try {
            for (i in 0 until promptData.size) {
                val helpCommand = promptData[i]
                val helpPromptModel = HelpPromptModel()
                helpPromptModel.name = helpCommand.name
                helpPromptModel.description = helpCommand.description
                helpPromptModel.prompt = helpCommand.prompt
                helpPromptModel.tags = helpCommand.tags
                promptList.add(helpPromptModel)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            throw Exception(CommonConstants.ERROR_MSG_JSON)
        }
        return promptList
    }

    fun stringToTime(strTime: String): Time {
        val list = strTime.split(':')
        val hour = list[0].toInt()
        val minute = list[1].toInt()
        val time = Time(hour, minute, 0)
        return time
    }
}