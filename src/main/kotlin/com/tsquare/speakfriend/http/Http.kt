package com.tsquare.speakfriend.http

import com.tsquare.speakfriend.api.ApiResponse
import com.tsquare.speakfriend.auth.Auth
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class Http {
    private val base = "http://speakfriend-api.local/api"

    fun get(endpoint: String): ApiResponse {
        val location = "$base/$endpoint?XDEBUG_SESSION_START=PHPSTORM"
        val url = URL(location)

        return sendRequest(url, "GET", "")
    }

    fun get(endpoint: String, parameters: String): ApiResponse {
        val location = "$base/$endpoint?XDEBUG_SESSION_START=PHPSTORM"
        val url = URL(location)

        return sendRequest(url, "GET", parameters)
    }

    fun post(endpoint: String, parameters: String): ApiResponse {
        val location = "$base/$endpoint?XDEBUG_SESSION_START=PHPSTORM"
        val url = URL(location)

        return sendRequest(url, "POST", parameters)
    }

    fun sendJson(endpoint: String, parameters: String): ApiResponse {
        val location = "$base/$endpoint?XDEBUG_SESSION_START=PHPSTORM"
        val url = URL(location)

        return sendRequest(url, "JSON", parameters)
    }

    private fun sendRequest(url: URL, method: String, parameters: String): ApiResponse {
        val auth = Auth()

        val connection = url.openConnection()
        if (auth.getApiToken().isNotEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + auth.getApiToken());
        }

        var httpMethod = method
        if (method == "JSON") {
            httpMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")
        }

        if (httpMethod == "POST") {
            connection.doOutput = true
        }

        with(connection as HttpURLConnection) {

            requestMethod = httpMethod

            if (requestMethod != "GET") {
                val wr = OutputStreamWriter(outputStream);
                wr.write(parameters);
                wr.flush();
            }

            ApiResponse.statusCode = responseCode
            ApiResponse.responseMessage = responseMessage

            if (responseCode == 200) {
                inputStream.bufferedReader().use {
                    val response = StringBuffer()

                    it.lines().forEach { line ->
                        response.append(line)
                    }

                    ApiResponse.responseBody = response.toString()

                    return ApiResponse
                }
            }

            errorStream.bufferedReader().use {
                val response = StringBuffer()

                it.lines().forEach { line ->
                    response.append(line)
                }

                ApiResponse.responseBody = response.toString()

                val parser = JSONParser()

                val requestObject = parser.parse(response.toString()) as JSONObject

                try {
                    val errors = requestObject.get("errors") as JSONObject
                    ApiResponse.setApiErrors(errors)
                } catch (e: Exception) {}

                return ApiResponse
            }
        }
    }
}
