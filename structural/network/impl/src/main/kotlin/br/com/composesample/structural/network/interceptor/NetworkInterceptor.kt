package br.com.composesample.structural.network.interceptor

import br.com.composesample.structural.core.ext.plusSpacing
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource.Companion.ACCESS_TOKEN
import br.com.composesample.structural.network.NetworkConstants.AUTHORIZATION_HEADER
import br.com.composesample.structural.network.NetworkConstants.BEARER_TOKEN_TYPE
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal class NetworkInterceptor(
    private val preferencesDataSource: PreferencesDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { preferencesDataSource.getString(ACCESS_TOKEN).orEmpty() }
        val request = chain.request().newBuilder().header(
            AUTHORIZATION_HEADER, BEARER_TOKEN_TYPE.plusSpacing(accessToken)
        ).build()
        return chain.proceed(request)
    }

}
