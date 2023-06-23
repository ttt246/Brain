package com.matthaigh27.chatgptwrapper.data.models

data class ContactModel(
    var contactId: String = String(),
    var displayName: String = String(),
    var phoneNumbers: ArrayList<String> = ArrayList(),
    var status: String = String()
)
