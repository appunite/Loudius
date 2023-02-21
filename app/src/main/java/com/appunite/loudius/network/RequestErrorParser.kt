package com.appunite.loudius.network

interface RequestErrorParser {

    operator fun invoke(responseCode: Int, responseMessage: String): Exception
}
