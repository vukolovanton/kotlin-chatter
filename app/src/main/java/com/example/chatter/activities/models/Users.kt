package com.example.chatter.activities.models

public class Users {
     var display_name: String? = null
     var image: String? = null
     var status: String? = null
    var userId: String? = null

    fun Users() {}

    fun Users(display_name: String, image: String, status: String, userId: String) {
        this.display_name = display_name
        this.image = image
        this.status = status
        this.userId = userId
    }


}