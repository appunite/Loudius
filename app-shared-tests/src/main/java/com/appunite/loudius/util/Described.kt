package com.appunite.loudius.util

/**
 * Adds description to a test, so failures are better recorded
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
    AssertionError("Error in step: \"$step\"", cause) {
}
