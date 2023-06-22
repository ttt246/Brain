package com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces

interface ChatMessageInterface {
    fun sentSms(phoneNumber: String, message: String)
    fun canceledSms()
    fun sentHelpPrompt(prompt: String)
    fun canceledHelpPrompt()
    fun doVoiceCall(phoneNumber: String)
    fun doVideoCall(phoneNumber: String)
    fun sendSmsWithPhoneNumber(phoneNumber: String)
    fun pickImage(isSuccess: Boolean, data: ByteArray? = null)
}