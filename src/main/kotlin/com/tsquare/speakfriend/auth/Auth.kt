package com.tsquare.speakfriend.auth

import com.tsquare.speakfriend.database.user.User
import com.tsquare.speakfriend.crypt.Crypt

class Auth {
    fun checkIn(user: String, pass: String): Boolean {
        val userModel = User()
        val match = userModel.get(user) ?: return false

        if(!Crypt.match(pass, match.pass)) {
            return false
        }

        CurrentUser.userId = match.id.value
        CurrentUser.userName = match.name
        CurrentUser.userKey = Crypt.generateKey(match.pass, pass).toString()

        return true
    }

    fun checkOut() {
        CurrentUser.userId = 0
        CurrentUser.userName = ""
        CurrentUser.userKey = ""
    }

    fun getId(): Int {
        return CurrentUser.userId
    }

    fun getName(): String {
        return CurrentUser.userName
    }

    fun getKey(): String {
        return CurrentUser.userKey
    }

    fun getVersion(): String {
        return CurrentUser.version
    }
}
