package br.com.composesample.structural.core.ext

import br.com.composesample.structural.network.exception.HttpException
import br.com.composesample.structural.network.exception.SampleException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

fun <T> Flow<T>.onError(
    ignoreUnauthorized: Boolean = true,
    block: SampleException.() -> Unit
): Flow<T> = catch {
    if (it is CancellationException) throw it
    if (ignoreUnauthorized && (it as? HttpException)?.statusCode == HTTP_UNAUTHORIZED) {
        return@catch
    }
    block(it.toSampleException())
}

/** Collects values and consumes upstream failures while preserving coroutine cancellation. */
suspend fun <T> Flow<T>.onSuccess(block: suspend T.() -> Unit) {
    catch {
        if (it is CancellationException) throw it
    }.collect { block(it) }
}

/**
 * Collects the flow and invokes [block] once on completion, failure or cancellation.
 * Cleanup runs in a non-cancellable context so it may suspend; the original failure or
 * cancellation propagates afterward unless cleanup itself throws.
 */
suspend fun Flow<*>.onFinish(block: suspend () -> Unit) {
    try {
        collect {}
    } finally {
        withContext(NonCancellable) {
            block()
        }
    }
}

/** Collects without consuming values; upstream failures are ignored and cancellation propagates. */
suspend fun Flow<*>.launch() {
    catch {
        if (it is CancellationException) throw it
    }.collect {}
}

suspend fun <T> Flow<T>.collectResult(): T {
    var result: T? = null
    collect {
        result = it
    }
    return result ?: throw Exception()
}

suspend fun <T> Flow<T>.collectResultSafely(): T? {
    var result: T? = null
    onError {
        result = null
    }.onSuccess {
        result = this
    }
    return result
}

fun <T, R> Flow<T>.update(block: suspend T.() -> R): Flow<R> = transform { emit(block(it)) }
