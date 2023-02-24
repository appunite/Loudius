package com.appunite.loudius.network.utils

import java.io.IOException

sealed class WebException : Exception() {

    /**
     * Represents exception which comes from backend.
     */
    data class UnknownError(val code: Int, override val message: String?) : WebException()

    /**
     * Represents web exception which can be thrown during network communication.
     * For example [IOException].
     */
    data class NetworkError(override val cause: Throwable? = null) : WebException()
}
