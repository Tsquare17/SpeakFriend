package com.tsquare.speakfriend.auth

object CurrentUser
{
    var userId: Int = 0
    var userName: String = ""
    var userKey: String = ""
    var version: Int = 0
    var apiEncryptionKey: String = ""
    var apiHash: String = ""
    var apiToken: String = ""
    var apiPass: String = ""
}
