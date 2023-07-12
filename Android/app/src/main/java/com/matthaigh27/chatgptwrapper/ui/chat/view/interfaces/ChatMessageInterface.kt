package com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces

import com.matthaigh27.chatgptwrapper.data.models.chat.MailModel

/**
 * This interface is a callback function that retrieves the results of chat widgets.
 */
interface ChatMessageInterface {
    fun sentSms(phoneNumber: String, message: String)
    fun canceledSms()
    fun sentHelpPrompt(prompt: String)
    fun canceledHelpPrompt()
    fun doVoiceCall(phoneNumber: String)
    fun doVideoCall(phoneNumber: String)
    fun sendSmsWithPhoneNumber(phoneNumber: String)
    fun pickImage(isSuccess: Boolean, data: ByteArray? = null)
    fun setAlarm(hours: Int, minutes: Int, label: String)
    fun cancelAlarm()
    fun readMail(from: String, password: String, imap_folder: String)
    fun sendMail(
        from: String,
        password: String,
        to: String,
        subject: String,
        body: String,
        isInbox: Boolean,
        filename: String,
        fileContent: String
    )

    fun readMailInDetail(mail: MailModel)
}