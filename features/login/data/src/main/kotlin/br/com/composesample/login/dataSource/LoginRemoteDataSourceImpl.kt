package br.com.composesample.login.dataSource

import br.com.composesample.login.service.LoginService

class LoginRemoteDataSourceImpl(
    private val loginService: LoginService
) : LoginRemoteDataSource {

    override suspend fun refreshToken(refreshToken: String) = loginService.refreshToken(refreshToken)

}
