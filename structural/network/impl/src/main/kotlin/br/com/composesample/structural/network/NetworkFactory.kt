package br.com.composesample.structural.network

import android.content.Context
import br.com.composesample.login.repository.LoginRepository
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource
import br.com.composesample.structural.network.authenticator.NetworkAuthenticator
import br.com.composesample.structural.network.connectivity.NetworkConnectivityProvider
import br.com.composesample.structural.network.connectivity.NetworkConnectivityProviderImpl
import br.com.composesample.structural.session.AppSessionProvider
import br.com.composesample.structural.network.interceptor.NetworkInterceptor
import okhttp3.Authenticator
import okhttp3.Interceptor

fun createNetworkConnectivityProvider(context: Context): NetworkConnectivityProvider =
    NetworkConnectivityProviderImpl(context)

fun createNetworkAuthenticator(
    appSessionProvider: AppSessionProvider,
    loginRepository: LoginRepository,
    preferencesDataSource: PreferencesDataSource
): Authenticator = NetworkAuthenticator(appSessionProvider, loginRepository, preferencesDataSource)

fun createNetworkInterceptor(preferencesDataSource: PreferencesDataSource): Interceptor =
    NetworkInterceptor(preferencesDataSource)
