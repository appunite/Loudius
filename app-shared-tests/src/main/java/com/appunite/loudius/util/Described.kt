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

/**
 * Adds description to a test, so failures are better recorded
 *
 * Example usage:
 * ```kt
 *   description("Log-in") {
 *     description("Fill e-mail and password") {
 *        onView(withHint("E-mail")).perform(type("jacek.marchwicki@gmail.com"))
 *        onView(withHint("Password")).perform(type("password"))
 *     }
 *     description("Submit") {
 *        onView(withId(R.id.login_button)).perform(click())
 *     }
 *   }
 *   description("Ensure home screen is displayed") {
 *     onView(withClass("com.example.com.LoginScreen")).check(isDisplayed())
 *   }
 * ```
 *
 * In case of failure, you'll see:
 *
 * ```
 * Exception thrown DescriptionAssertionError("Error in step: Log-in -> Fill e-mail and password")
 * ```
 *
 * The exception is always thrown with the root cause attached.
 */
fun <T> description(description: String, lambda: () -> T): T {
    try {
        return lambda()
    } catch (error: DescriptionAssertionError) {
        throw DescriptionAssertionError("$description -> ${error.step}", error.cause!!)
    } catch (error: AssertionError) {
        throw DescriptionAssertionError(description, error)
    }
}

class DescriptionAssertionError(val step: String, cause: Throwable) :
    AssertionError("Error in step: \"$step\"", cause)
