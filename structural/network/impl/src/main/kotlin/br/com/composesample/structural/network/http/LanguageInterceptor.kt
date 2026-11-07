package br.com.composesample.structural.network.http

import br.com.composesample.structural.localization.AppRegionProvider
import okhttp3.Interceptor
import okhttp3.Response

internal class LanguageInterceptor(private val appRegionProvider: AppRegionProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        request.url.queryParameter(LANGUAGE_PARAMETER)?.let { return chain.proceed(request) }
        val url =
            request.url
                .newBuilder()
                .addQueryParameter(LANGUAGE_PARAMETER, appRegionProvider.region.value.languageTag)
                .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }

    private companion object {
        const val LANGUAGE_PARAMETER = "language"
    }
}
