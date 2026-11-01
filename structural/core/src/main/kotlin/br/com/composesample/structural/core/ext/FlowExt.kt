package br.com.composesample.structural.core.ext

import br.com.composesample.structural.network.exception.SampleException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform
import br.com.composesample.structural.network.exception.HttpException

fun <T> Flow<T>.onError(
    ignoreUnauthorized: Boolean = true,
    block: SampleException.() -> Unit
): Flow<T> = catch {
    if (ignoreUnauthorized && (it as? HttpException)?.statusCode == HTTP_UNAUTHORIZED) {
        return@catch
    }
    block(it.toSampleException())
}

/** Collects emitted values and ignores upstream failures. Coroutine cancellation still propagates. */
suspend fun <T> Flow<T>.onSuccess(block: suspend T.() -> Unit) {
    catch { }.collect { block(it) }
}

suspend fun Flow<*>.onFinish(block: suspend () -> Unit) {
    runCatching {
        collect { block.invoke() }
    }.onFailure {
        block.invoke()
    }
}

suspend fun Flow<*>.launch() {
    runCatching { collect {} }
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
