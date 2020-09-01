package com.tsquare.speakfriend.http

import com.tsquare.speakfriend.api.ApiResponse
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Http {
    private val base = "http://speakfriend-api.local/api"

    fun get(endpoint: String): ApiResponse {
        val location = "$base/$endpoint"
        val url = URL(location)

        return sendRequest(url, "GET", "")
    }

    fun get(endpoint: String, parameters:String): ApiResponse {
        val location = "$base/$endpoint"
        val url = URL(location)

        return sendRequest(url, "GET", parameters)
    }

    fun post(endpoint: String, parameters:String): ApiResponse {
        val location = "$base/$endpoint"
        val url = URL(location)

        return sendRequest(url, "POST", parameters)
    }

    private fun sendRequest(url: URL, method: String, parameters: String): ApiResponse {
        val connection = url.openConnection()
        connection.doOutput = true
        with(connection as HttpURLConnection) {

            requestMethod = method

            val wr = OutputStreamWriter(outputStream);
            wr.write(parameters);
            wr.flush();

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

                ApiResponse.errors = requestObject.get("errors") as JSONObject

                return ApiResponse
            }
        }
    }
}
