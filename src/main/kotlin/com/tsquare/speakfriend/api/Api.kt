package com.tsquare.speakfriend.api;

import com.tsquare.speakfriend.http.Http
import java.net.URLEncoder

class Api {
    fun register(name: String, email: String, password: String, confirm_password: String): ApiResponse {
        var parameters = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
        parameters += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
        parameters += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
        parameters += "&" + URLEncoder.encode("password_confirmation", "UTF-8") + "=" +
                URLEncoder.encode(confirm_password, "UTF-8")

        val http = Http()

        return http.sendPost("register", parameters)
    }

    fun login(email: String, password: String): ApiResponse {
        var parameters = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
        parameters += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")

        val http = Http()

        return http.sendPost("register", parameters)
    }
}
