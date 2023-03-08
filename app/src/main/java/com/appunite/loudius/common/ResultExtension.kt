package com.appunite.loudius.common

inline fun <T, G> Result<T>.flatMap(mapper: (value: T) -> Result<G>): Result<G> =
    fold(onSuccess = mapper, onFailure = { Result.failure(it) })
