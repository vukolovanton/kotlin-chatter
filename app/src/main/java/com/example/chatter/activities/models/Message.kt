package com.example.chatter.activities.models

class Message() {
    var id: String? = null
    var text: String? = null
    var name: String? = null

    constructor(id: String?, text: String?, name: String): this() {
        this.id = id
        this.text = text
        this.name = name
    }
}