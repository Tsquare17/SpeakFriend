package com.tsquare.speakfriend.auth

import com.tsquare.speakfriend.database.user.User
import com.tsquare.speakfriend.crypt.Crypt

class Auth {
    fun checkIn(user: String, pass: String): Boolean {
        val userModel = User()
        val match = userModel.get(user) ?: return false

        val key = Crypt.hash(match.pass)
        if(Crypt.hash(pass) != key) {
            return false
        }

        CurrentUser.userId = match.id.value
        CurrentUser.userName = match.name
        CurrentUser.userKey = Crypt.hash(key)
        return true;
    }

    fun getId(): Int {
        return CurrentUser.userId;
    }
}
