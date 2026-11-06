package br.com.composesample.structural.network.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class HttpClientFactory {

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
        gson = createGson(),
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

    private fun createGson(): Gson =
        GsonBuilder()
            .setStrictness(Strictness.STRICT)
            .setFieldNamingStrategy(::resolveJsonName)
            .create()

    private fun resolveJsonName(field: Field): String {
        val name = field.getAnnotation(JsonName::class.java)?.value
        if (name.isNullOrBlank()) {
            throw JsonParseException(
                "Field ${field.declaringClass.name}.${field.name} requires @JsonName"
            )
        }
        return name
    }

    private companion object {

        const val CALL_TIMEOUT_SECONDS = 30L

        const val READ_TIMEOUT_SECONDS = 30L

    }

}
