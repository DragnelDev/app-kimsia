package com.example.kimsia.domain.util

import kotlin.coroutines.cancellation.CancellationException

/** Como runCatching, pero no se "traga" la cancelación de corrutinas. */
suspend inline fun <T> suspendRunCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
