package br.com.composesample.login.repository

import br.com.composesample.login.analytics.LoginAnalytics
import br.com.composesample.login.dataSource.LoginRemoteDataSource
import kotlinx.coroutines.CancellationException

class LoginRepositoryImpl(
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val analytics: LoginAnalytics
) : LoginRepository {

    override suspend fun refreshToken(refreshToken: String): String? {
        analytics.tokenRefreshStarted()
        return try {
            val accessToken = loginRemoteDataSource.refreshToken(refreshToken).accessToken
            analytics.tokenRefreshFinished(succeeded = !accessToken.isNullOrBlank())
            accessToken
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            analytics.tokenRefreshFinished(succeeded = false)
            throw exception
        }
    }

}
