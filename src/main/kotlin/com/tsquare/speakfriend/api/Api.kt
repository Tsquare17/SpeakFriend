package com.tsquare.speakfriend.api;

import com.fasterxml.jackson.databind.ObjectMapper
import com.tsquare.speakfriend.http.Http
import java.net.http.HttpResponse
import java.util.HashMap

class Api {
    fun register(email: String, password: String): ApiResponse {
        val values: HashMap<String?, String?> = getUserPassHashMap(email, password)

        val objectMapper = ObjectMapper()
        val requestBody: String = objectMapper
                .writeValueAsString(values)

        val http = Http()

        val response = http.post("register", requestBody)

        return setApiResponseBody(response)
    }

    fun login(email: String, password: String): ApiResponse {
        val values: HashMap<String?, String?> = getUserPassHashMap(email, password)

        val objectMapper = ObjectMapper()
        val requestBody: String = objectMapper
                .writeValueAsString(values)

        val http = Http()

        val response = http.post("login", requestBody)

        return setApiResponseBody(response)
    }

    private fun setApiResponseBody(response: HttpResponse<String>?): ApiResponse {
        if (response != null) {
            ApiResponse.responseBody = response.body()
            ApiResponse.responseHeaders = response.headers()
            ApiResponse.statusCode = response.statusCode()
        }

        return ApiResponse
    }

    private fun getUserPassHashMap(email: String, password: String): HashMap<String?, String?> {
        return object : HashMap<String?, String?>() {
            init {
                put("email", email)
                put("password", password)
            }
        }
    }
}
