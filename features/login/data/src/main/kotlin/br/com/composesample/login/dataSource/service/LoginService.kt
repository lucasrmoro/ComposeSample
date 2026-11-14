package br.com.composesample.login.dataSource.service

import br.com.composesample.login.dto.LoginDTO

interface LoginService {

    suspend fun refreshToken(refreshToken: String): LoginDTO

}
