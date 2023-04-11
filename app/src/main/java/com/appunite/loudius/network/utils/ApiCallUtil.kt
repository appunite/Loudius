/*
 * Copyright 2023 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.loudius.network.utils

import com.appunite.loudius.network.model.error.DefaultErrorResponse
import com.google.gson.Gson
import com.google.gson.JsonParseException
import java.io.IOException
import javax.inject.Inject
import org.json.JSONException
import retrofit2.HttpException

class ApiRequester @Inject constructor(private val gson: Gson) {

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
        } catch (throwable: JsonParseException) {
            Result.failure(WebException.UnknownError(null, throwable.message))
        }
    }

    private fun getApiErrorMessageIfExist(throwable: HttpException) = try {
        val errorResponse = gson.fromJson(
            throwable.response()?.errorBody()?.charStream(),
            DefaultErrorResponse::class.java,
        )
        errorResponse.message
    } catch (throwable: JSONException) {
        null
    }

    object DefaultErrorParser : RequestErrorParser {

        override fun invoke(responseCode: Int, responseMessage: String): Exception =
            WebException.UnknownError(responseCode, responseMessage)
    }

}
