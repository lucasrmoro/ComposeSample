package br.com.composesample.structural.network.http

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal suspend fun <T> Call<ResponseBody>.awaitDecoded(
    decode: (Response<ResponseBody>) -> T
): T = suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation { cancel() }
    enqueue(object : Callback<ResponseBody> {

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            try {
                val result = response.body().use {
                    response.errorBody().use {
                        if (!continuation.isActive) return
                        decode(response)
                    }
                }
                continuation.resume(result)
            } catch (exception: Throwable) {
                continuation.resumeWithException(exception)
            }
        }

        override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
            continuation.resumeWithException(throwable)
        }

    })
}
