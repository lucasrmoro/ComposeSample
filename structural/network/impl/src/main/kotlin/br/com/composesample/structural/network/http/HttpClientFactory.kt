package br.com.composesample.structural.network.http

import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class HttpClientFactory internal constructor(
    private val gson: Gson
) {

    private val downloadClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build()
    }

    fun createAuthenticated(
        baseUrl: String,
        dispatcher: CoroutineDispatcher,
        authenticator: Authenticator,
        interceptor: Interceptor
    ): HttpClient = create(
        baseUrl,
        dispatcher,
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)
    )

    fun createUnauthenticated(baseUrl: String, dispatcher: CoroutineDispatcher): HttpClient =
        create(baseUrl, dispatcher, OkHttpClient.Builder())

    internal fun createDownloadService(baseUrl: String): RetrofitHttpService =
        createService(baseUrl, downloadClient)

    private fun create(
        baseUrl: String,
        dispatcher: CoroutineDispatcher,
        builder: OkHttpClient.Builder
    ): HttpClient = RetrofitHttpClient(
        service = createService(baseUrl, createOkHttpClient(builder)),
        gson = gson,
        dispatcher = dispatcher
    )

    private fun createOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient =
        builder
            .withHttpLogging()
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    private fun createService(baseUrl: String, client: OkHttpClient): RetrofitHttpService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
            .create(RetrofitHttpService::class.java)

    private companion object {

        const val CALL_TIMEOUT_SECONDS = 30L

        const val READ_TIMEOUT_SECONDS = 30L

    }

}

fun createHttpClientFactory(serialization: NetworkSerialization): HttpClientFactory =
    HttpClientFactory(serialization.gson)
