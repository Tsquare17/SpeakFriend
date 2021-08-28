package com.tsquare.speakfriend.auth

import com.tsquare.speakfriend.crypt.Crypt
import com.tsquare.speakfriend.database.account.AccountList
import com.tsquare.speakfriend.database.user.User
import com.tsquare.speakfriend.settings.Options
import com.tsquare.speakfriend.state.State

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

        val dbVersion = Options.get("db_version")
        if (dbVersion == "") {
            CurrentUser.version = 0
        } else {
            CurrentUser.version = dbVersion.toInt()
        }

        return true
    }

    fun checkOut() {
        CurrentUser.userId = 0
        CurrentUser.userName = ""
        CurrentUser.userKey = ""
        CurrentUser.version = 0

        AccountList.clear();
        State.isDirtyAccounts = 1
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

    fun getVersion(): Int {
        val dbVersion = Options.get("db_version")
        if (dbVersion == "") {
            CurrentUser.version = 0
        } else {
            CurrentUser.version = dbVersion.toInt()
        }
        return CurrentUser.version
    }
}
