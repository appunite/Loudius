package com.appunite.loudius.network.utils

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    errorParser: RequestErrorParser = DefaultErrorParser,
    apiCall: suspend () -> T
): Result<T> {
    return try {
        val response = apiCall()
        Result.success(response)
    } catch (throwable: HttpException) {
        Result.failure(errorParser(throwable.code(), throwable.message()))
    } catch (throwable: IOException) {
        Result.failure(WebException.NetworkError(throwable))
    }
}

object DefaultErrorParser : RequestErrorParser {

    override fun invoke(responseCode: Int, responseMessage: String): Exception =
        WebException.UnknownError(responseCode, responseMessage)
}
