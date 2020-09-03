package com.tsquare.speakfriend.http

import com.tsquare.speakfriend.api.ApiResponse
import com.tsquare.speakfriend.auth.Auth
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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

        val request = sendRequest(url, "POST", parameters)


        return request
    }

    private fun sendRequest(url: URL, method: String, parameters: String): ApiResponse {
        var params = parameters
        val auth = Auth()
        if (auth.getApiToken().isNotEmpty()) {
            if (params.isNotEmpty()) {
                params += "&" + URLEncoder.encode("access_token", "UTF-8") + "=" + URLEncoder.encode(auth.getApiToken(), "UTF-8")
            } else {
                params = URLEncoder.encode("access_token", "UTF-8") + "=" + URLEncoder.encode(auth.getApiToken(), "UTF-8");
            }
        }

        val connection = url.openConnection()
        connection.doOutput = true
        with(connection as HttpURLConnection) {

            requestMethod = method

            val wr = OutputStreamWriter(outputStream);
            wr.write(params);
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

                val errors = requestObject.get("errors") as JSONObject

                ApiResponse.setApiErrors(errors)

                return ApiResponse
            }
        }
    }
}
