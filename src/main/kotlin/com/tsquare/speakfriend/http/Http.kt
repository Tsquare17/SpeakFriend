package com.tsquare.speakfriend.http

import com.tsquare.speakfriend.auth.CurrentUser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Http {
    private val client: HttpClient = HttpClient.newHttpClient()
    val version = if (CurrentUser.version != "") CurrentUser.version else "v1"
    private val base = "http://speakfriend-api.local/api"

    fun get(): HttpResponse<String>? {
        return get("")
    }

    fun get(endpoint: String?): HttpResponse<String>? {
        val location = if (endpoint == "") "$base/$version" else "$base/$version/$endpoint"
        val request = HttpRequest.newBuilder(URI(location))
                .GET()
                .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response
    }

    fun post(endpoint: String, requestBody: String): HttpResponse<String>? {
        val location = "$base/$version/$endpoint"

        val request = HttpRequest.newBuilder(URI(location))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response
    }
}
