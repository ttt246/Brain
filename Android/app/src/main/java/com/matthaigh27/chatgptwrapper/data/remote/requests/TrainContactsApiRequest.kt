package com.matthaigh27.chatgptwrapper.data.remote.requests

import com.matthaigh27.chatgptwrapper.data.models.ContactModel
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys

data class TrainContactsApiRequest(
    val contacts: ArrayList<ContactModel>,
    val confs: Keys
)