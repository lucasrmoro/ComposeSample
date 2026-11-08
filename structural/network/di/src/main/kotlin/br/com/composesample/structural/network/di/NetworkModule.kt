package br.com.composesample.structural.network.di

import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider
import br.com.composesample.structural.network.createNetworkAuthenticator
import br.com.composesample.structural.network.createNetworkConnectivityProvider
import br.com.composesample.structural.network.createNetworkInterceptor
import br.com.composesample.structural.network.http.BinaryDownloadProvider
import br.com.composesample.structural.network.http.ConfiguredHttpClientProvider
import br.com.composesample.structural.network.http.HttpClient
import br.com.composesample.structural.network.http.HttpClientConfiguration
import br.com.composesample.structural.network.http.JsonCodec
import br.com.composesample.structural.network.http.NetworkComponents
import br.com.composesample.structural.network.http.createAuthenticatedHttpClient
import br.com.composesample.structural.network.http.createBinaryDownloadProvider
import br.com.composesample.structural.network.http.createConfiguredHttpClientProvider
import br.com.composesample.structural.network.http.createJsonCodec
import br.com.composesample.structural.network.http.createUnauthenticatedHttpClient
import okhttp3.Authenticator
import okhttp3.Interceptor

object NetworkModule {
    private val networkModule = dependencyModule {
        singleton<BinaryDownloadProvider> {
            createBinaryDownloadProvider(
                coroutineDispatcher = dispatcherIO,
                components = get()
            )
        }
        singleton<JsonCodec> { createJsonCodec(components = get()) }
        singleton<ConfiguredHttpClientProvider> {
            createConfiguredHttpClientProvider(
                coroutineDispatcher = dispatcherIO,
                appRegionProvider = get(),
                components = get()
            )
        }
        singleton {
            HttpClientConfiguration(
                baseUrl = br.com.composesample.structural.network.impl.BuildConfig.API_BASE_URL
            )
        }
        singleton<NetworkConnectivityProvider> { createNetworkConnectivityProvider(context = get()) }
        singleton<Authenticator> {
            createNetworkAuthenticator(
                appSessionProvider = get(),
                loginRepository = get(),
                preferencesDataSource = get()
            )
        }
        singleton<Interceptor> { createNetworkInterceptor(preferencesDataSource = get()) }
        singleton { NetworkComponents() }
        singleton<HttpClient>(AuthenticatedHttpClient) {
            createAuthenticatedHttpClient(
                components = get(),
                baseUrl = get<HttpClientConfiguration>().baseUrl,
                dispatcher = dispatcherIO,
                authenticator = get(),
                interceptor = get()
            )
        }
        singleton<HttpClient>(UnauthenticatedHttpClient) {
            createUnauthenticatedHttpClient(
                components = get(),
                baseUrl = get<HttpClientConfiguration>().baseUrl,
                dispatcher = dispatcherIO
            )
        }
    }

    fun init() = loadModules(networkModule)
}
