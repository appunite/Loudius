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

package com.appunite.loudius.util

import com.appunite.loudius.BuildConfig
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.time.SystemTimeProvider
import strikt.api.expectThat
import strikt.assertions.isTrue

const val githubUserPassword = BuildConfig.LOUDIUS_GITHUB_USER_PASSWORD
const val githubUserName = BuildConfig.LOUDIUS_GITHUB_USER_NAME
const val githubOtpSecret = BuildConfig.LOUDIUS_GITHUB_USER_OTP_SECRET

/**
 * Generates Google Authenticator One Time Password for Log-in via Github
 */
fun generateOtp(): String {
    val timeProvider = SystemTimeProvider()
    val codeGenerator = DefaultCodeGenerator()
    val code = codeGenerator.generate(githubOtpSecret, Math.floorDiv(timeProvider.time, 30L))
    val verifier = DefaultCodeVerifier(codeGenerator, timeProvider)

    expectThat(verifier.isValidCode(githubOtpSecret, code)).isTrue()
    return code
}
