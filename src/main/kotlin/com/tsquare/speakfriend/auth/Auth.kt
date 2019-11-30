package com.tsquare.speakfriend.auth

class Auth {

    fun check(user: String, pass: String): Boolean {
        return true;
    }

    fun getId(): Int {
        return 1;
    }

    fun encrypt(userId: Int, pass: String): String {
        return pass;
    }
}
