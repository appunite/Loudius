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

import java.io.IOException

sealed class WebException : Exception() {

    /**
     * Represents exception which comes from backend.
     */
    data class UnknownError(val code: Int?, override val message: String?) : WebException()

    /**
     * Represents web exception which can be thrown during network communication.
     * For example [IOException].
     */
    data class NetworkError(override val cause: Throwable? = null) : WebException()

    // suggestion: having this exception that can happen in one place kept in common class, is not the best solution.
    // if the app grows much bigger, than we will have here all exception for all API endpoints.
    /**
     * Thrown during authorization with incorrect verification code.
     */
    object BadVerificationCodeException : WebException()
}
