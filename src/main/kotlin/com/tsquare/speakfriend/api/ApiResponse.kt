package com.tsquare.speakfriend.api

import org.json.simple.JSONObject

object ApiResponse
{
    lateinit var responseBody: String
    lateinit var responseMessage: String
    var statusCode: Int = 0
    lateinit var errors: JSONObject
}
