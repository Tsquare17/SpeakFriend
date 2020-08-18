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

    fun get(): String? {
        return get("")
    }

    fun get(endpoint: String?): String? {
        val location = if (endpoint == "") "$base/$version" else "$base/$version/$endpoint"
        val request = HttpRequest.newBuilder(URI(location))
                .GET()
                .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body()
    }
}
