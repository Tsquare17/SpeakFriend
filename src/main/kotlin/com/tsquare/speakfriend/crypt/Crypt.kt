package com.tsquare.speakfriend.crypt

object Crypt
{
    @JvmStatic
    fun encrypt(userId: Int, pass: String): String {
        return pass;
    }

    @JvmStatic
    fun hash(pass: String): String {
        return pass
    }
}
