package br.com.composesample.structural.network.authenticator

import br.com.composesample.structural.core.ext.plusSpacing
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource.Companion.ACCESS_TOKEN
import br.com.composesample.structural.local_storage.prefs.PreferencesDataSource.Companion.REFRESH_TOKEN
import br.com.composesample.structural.network.AccessTokenRefresher
import br.com.composesample.structural.network.NetworkConstants.AUTHORIZATION_HEADER
import br.com.composesample.structural.network.NetworkConstants.BEARER_TOKEN_TYPE
import br.com.composesample.structural.session.AppSessionProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Route

internal class NetworkAuthenticator(
    private val appSessionProvider: AppSessionProvider,
    private val accessTokenRefresher: AccessTokenRefresher,
    private val preferencesDataSource: PreferencesDataSource
) : Authenticator {

    override fun authenticate(route: Route?, response: Response) =
        synchronized(this) {
            runBlocking {
                val localAccessToken = preferencesDataSource.getString(ACCESS_TOKEN).orEmpty()
                val shouldRefreshAccessToken = response.shouldRefreshAccessToken(localAccessToken)
                val accessToken = if (shouldRefreshAccessToken) {
                    getRemoteAccessToken()
                } else {
                    localAccessToken
                }

                when {
                    accessToken.isEmpty() || accessToken == FETCH_REMOTE_REFRESH_TOKEN_ERROR -> {
                        if (shouldRefreshAccessToken && localAccessToken != accessToken) {
                            preferencesDataSource.putString(ACCESS_TOKEN, accessToken)
                            appSessionProvider.expireSession()
                        }
                        null
                    }

                    else -> {
                        preferencesDataSource.putString(ACCESS_TOKEN, accessToken)
                        response.request.newBuilder()
                            .header(AUTHORIZATION_HEADER, accessToken.plusBearerTokenType())
                            .build()
                    }
                }
            }
        }

    private suspend fun getRemoteAccessToken(): String =
        runCatching {
            accessTokenRefresher.refreshAccessToken(preferencesDataSource.getString(REFRESH_TOKEN).orEmpty())
        }.getOrNull() ?: FETCH_REMOTE_REFRESH_TOKEN_ERROR

    private fun Response.shouldRefreshAccessToken(localAccessToken: String): Boolean =
        request.header(AUTHORIZATION_HEADER) == localAccessToken

    private fun String.plusBearerTokenType() = BEARER_TOKEN_TYPE.plusSpacing(this)

    companion object {
        private const val FETCH_REMOTE_REFRESH_TOKEN_ERROR = "FETCH_REMOTE_REFRESH_TOKEN_ERROR"
    }
}
