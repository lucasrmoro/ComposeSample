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
import br.com.composesample.structural.network.http.HttpClient
import br.com.composesample.structural.network.http.HttpClientConfiguration
import br.com.composesample.structural.network.http.HttpClientFactory
import br.com.composesample.structural.network.impl.BuildConfig as NetworkBuildConfig
import okhttp3.Authenticator
import okhttp3.Interceptor

object NetworkModule {

    private val networkModule = dependencyModule {
        singleton { HttpClientConfiguration(baseUrl = NetworkBuildConfig.API_BASE_URL) }
        singleton<NetworkConnectivityProvider> { createNetworkConnectivityProvider(context = get()) }
        singleton<Authenticator> {
            createNetworkAuthenticator(
                appSessionProvider = get(),
                loginRepository = get(),
                preferencesDataSource = get()
            )
        }
        singleton<Interceptor> { createNetworkInterceptor(preferencesDataSource = get()) }
        singleton { HttpClientFactory() }
        singleton<HttpClient>(AuthenticatedHttpClient) {
            get<HttpClientFactory>().createAuthenticated(
                baseUrl = get<HttpClientConfiguration>().baseUrl,
                dispatcher = dispatcherIO,
                authenticator = get(),
                interceptor = get()
            )
        }
        singleton<HttpClient>(UnauthenticatedHttpClient) {
            get<HttpClientFactory>().createUnauthenticated(
                baseUrl = get<HttpClientConfiguration>().baseUrl,
                dispatcher = dispatcherIO
            )
        }
    }

    fun init() {
        loadModules(networkModule)
    }

}
