package com.appunite.loudius.network

import java.io.IOException

sealed class WebException : Exception() {

    /**
     * Represents exception which comes from backend. In this project successful
     * response (status code = 200) often comes with error response (status
     * in response body is not equal to 200).
     */
    data class UnknownError(val code: Int, override val message: String?) : WebException()

    /**
     * Represents web exception which can be thrown during network communication.
     * For example [IOException].
     */
    data class NetworkError(override val cause: Throwable? = null) : WebException()
}
