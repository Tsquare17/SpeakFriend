package com.tsquare.speakfriend.api

import java.net.http.HttpHeaders

object ApiResponse
{
    lateinit var responseBody: String
    lateinit var responseHeaders: HttpHeaders
    var statusCode: Int = 0
}
