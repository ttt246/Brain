package com.matthaigh27.chatgptwrapper.utils.helpers.chat

object MailHelper {
    fun isGmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com")
    }
}