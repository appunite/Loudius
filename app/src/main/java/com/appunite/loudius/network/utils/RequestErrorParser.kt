package com.appunite.loudius.network.utils

interface RequestErrorParser {

    operator fun invoke(responseCode: Int, responseMessage: String): Exception
}
