package com.matthaigh27.chatgptwrapper.models.common

class ImagePromptModel {
    var id: String = ""
    var path: String = ""

    constructor(id: String, path: String) {
        this.id = id
        this.path = path
    }
}