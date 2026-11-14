package br.com.composesample.login.dataSource

import br.com.composesample.login.repository.LoginRepository
import br.com.composesample.structural.network.AccessTokenRefresher
import kotlinx.coroutines.flow.first

class LoginAccessTokenRefresher(
    private val loginRepository: LoginRepository
) : AccessTokenRefresher {

    override suspend fun refreshAccessToken(refreshToken: String): String? =
        loginRepository.refreshToken(refreshToken).first()

}
