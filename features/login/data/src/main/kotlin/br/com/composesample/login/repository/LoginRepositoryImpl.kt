package br.com.composesample.login.repository

import br.com.composesample.login.analytics.LoginAnalytics
import br.com.composesample.login.dataSource.LoginRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single

class LoginRepositoryImpl(
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val analytics: LoginAnalytics
) : LoginRepository {

    override fun refreshToken(refreshToken: String): Flow<String?> = flow {
        analytics.tokenRefreshStarted()
        try {
            val accessToken = loginRemoteDataSource.refreshToken(refreshToken).single().accessToken
            analytics.tokenRefreshFinished(succeeded = !accessToken.isNullOrBlank())
            emit(accessToken)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            analytics.tokenRefreshFinished(succeeded = false)
            throw exception
        }
    }

}
