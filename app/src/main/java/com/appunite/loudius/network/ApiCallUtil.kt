package com.appunite.loudius.network

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T : StatusTracker> safeApiCall(
    errorParser: RequestErrorParser = DefaultErrorParser,
    apiCall: suspend () -> Response<T>
): Result<T> {
    return try {
        handleSuccessfulCall(apiCall, errorParser)
    } catch (throwable: HttpException) {
        Result.failure(WebException.UnknownError(throwable.code(), throwable.message()))
    } catch (throwable: IOException) {
        Result.failure(WebException.NetworkError(throwable))
    }
}

private suspend fun <T : StatusTracker> handleSuccessfulCall(
    apiCall: suspend () -> Response<T>,
    errorParser: RequestErrorParser
): Result<T> {
    val response = apiCall()
    val body = response.body()
    return if (response.isSuccessful && body != null) {
        val status = body.status
        if (status == 200) {
            Result.success(body)
        } else {
            Result.failure(errorParser(status, body.message))
        }
    } else {
        Result.failure(errorParser(response.code(), response.message()))
    }
}

object DefaultErrorParser : RequestErrorParser {

    override fun invoke(responseCode: Int, responseMessage: String): Exception =
        WebException.UnknownError(responseCode, responseMessage)
}
