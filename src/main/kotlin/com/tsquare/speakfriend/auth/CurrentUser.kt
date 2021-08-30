package com.tsquare.speakfriend.auth

object CurrentUser
{
    var userId: Int = 0
    var userName: String = ""
    var userKey: String = ""
    var passHash: String = ""
    var version: Int = 0

    fun clear() {
        userId = 0
        userName = ""
        userKey = ""
        passHash = ""
        version = 0
    }
}
