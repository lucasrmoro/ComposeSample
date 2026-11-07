package br.com.composesample.structural.network.http

import br.com.composesample.structural.localization.AppRegionProvider
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient

internal class ConfiguredHttpClientProviderImpl(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val appRegionProvider: AppRegionProvider,
    private val httpClientFactory: HttpClientFactory
) : ConfiguredHttpClientProvider {

    override fun create(options: HttpClientOptions): HttpClient {
        val builder = OkHttpClient.Builder().followRedirects(false).followSslRedirects(false)
        builder.addInterceptor(
            Interceptor { chain ->
                val request = chain.request().newBuilder()
                options.headers.forEach { (name, value) -> request.header(name, value) }
                chain.proceed(request.build())
            }
        )
        if (options.useAppLanguage) builder.addInterceptor(LanguageInterceptor(appRegionProvider))
        return httpClientFactory.createCustomClient(options.baseUrl, coroutineDispatcher, builder)
    }

}

fun createConfiguredHttpClientProvider(
    coroutineDispatcher: CoroutineDispatcher,
    appRegionProvider: AppRegionProvider,
    components: NetworkComponents
): ConfiguredHttpClientProvider =
    ConfiguredHttpClientProviderImpl(coroutineDispatcher, appRegionProvider, components.httpClientFactory)
