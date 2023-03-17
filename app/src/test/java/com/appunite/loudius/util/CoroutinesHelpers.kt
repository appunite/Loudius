package com.appunite.loudius.utils

import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * A suspend function that never completes
 */
suspend fun <T> neverCompletingSuspension(): T = suspendCancellableCoroutine { }
