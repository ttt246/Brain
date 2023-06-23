package com.matthaigh27.chatgptwrapper.data.models

data class ContactModel(
    var id: String = "",
    var name: String = "",
    var phoneList: ArrayList<String> = ArrayList(),
    var status: String = ""
)
