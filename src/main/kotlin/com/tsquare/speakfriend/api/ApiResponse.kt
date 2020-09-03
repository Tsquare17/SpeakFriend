package com.tsquare.speakfriend.api

import org.json.simple.JSONArray
import org.json.simple.JSONObject

object ApiResponse
{
    lateinit var responseBody: String
    lateinit var responseMessage: String
    var statusCode: Int = 0
    lateinit var errors: StringBuilder

    fun setApiErrors(apiErrors: JSONObject) {
        val errorText = StringBuilder()
        val errorKeys: Set<*> = apiErrors.keys
        for (errorKey in errorKeys) {
            val key = errorKey.toString()
            val errorArray = apiErrors[key] as JSONArray
            for (k in errorArray.indices) {
                errorText.append(errorArray[k]).append(System.lineSeparator())
            }
        }
        errors = errorText
    }
}
