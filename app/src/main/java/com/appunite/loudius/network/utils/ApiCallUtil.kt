package com.appunite.loudius.network.utils

import com.google.gson.Gson
import java.io.IOException
import org.json.JSONException
import retrofit2.HttpException

suspend fun <T> safeApiCall(
    errorParser: RequestErrorParser = DefaultErrorParser,
    apiCall: suspend () -> T,
): Result<T> {
    return try {
        val response = apiCall()
        Result.success(response)
    } catch (throwable: HttpException) {
        val message = getApiErrorMessageIfExist(throwable)
        Result.failure(errorParser(throwable.code(), message ?: throwable.message()))
    } catch (throwable: IOException) {
        Result.failure(WebException.NetworkError(throwable))
    }
}

private fun getApiErrorMessageIfExist(throwable: HttpException) = try {
    val errorResponse = Gson().fromJson(
        throwable.response()?.errorBody()?.string(),
        DefaultErrorResponse::class.java
    )
    errorResponse.message
} catch (throwable: JSONException) {
    null
}

object DefaultErrorParser : RequestErrorParser {

    override fun invoke(responseCode: Int, responseMessage: String): Exception =
        WebException.UnknownError(responseCode, responseMessage)
}
