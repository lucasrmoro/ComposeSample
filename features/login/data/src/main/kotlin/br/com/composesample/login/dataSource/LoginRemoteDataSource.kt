package br.com.composesample.login.dataSource

import br.com.composesample.login.model.LoginDTO

interface LoginRemoteDataSource {

    suspend fun refreshToken(refreshToken: String): LoginDTO

}
