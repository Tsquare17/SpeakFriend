package com.tsquare.speakfriend.http

import com.tsquare.speakfriend.api.Api
import com.tsquare.speakfriend.api.ApiResponse
import com.tsquare.speakfriend.auth.CurrentUser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Http {
    private val client: HttpClient = HttpClient.newHttpClient()
    val version = if (CurrentUser.version != "") CurrentUser.version else "v1"
    private val base = "http://speakfriend-api.local/api"

    fun sendGet(endpoint: String, parameters:String): ApiResponse {
        val url = URL(endpoint)

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            ApiResponse.statusCode = responseCode
            ApiResponse.responseMessage = responseMessage

            inputStream.bufferedReader().use {
                val response = StringBuffer()

                it.lines().forEach { line ->
                    response.append(line)
                }

                ApiResponse.responseBody = response.toString()

                return ApiResponse
            }
        }
    }

    fun sendPost(endpoint: String, parameters:String): ApiResponse {
        val location = "$base/$endpoint"
        val url = URL(location)
        val connection = url.openConnection()
        connection.doOutput = true
        with(connection as HttpURLConnection) {

            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream);
            wr.write(parameters);
            wr.flush();

            ApiResponse.statusCode = responseCode
            ApiResponse.responseMessage = responseMessage

            inputStream.bufferedReader().use {
                val response = StringBuffer()

                it.lines().forEach { line ->
                    response.append(line)
                }

                ApiResponse.responseBody = response.toString()

                return ApiResponse
            }
        }
    }
}
