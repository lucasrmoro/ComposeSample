package br.com.composesample.login.service

import br.com.composesample.login.model.LoginDTO

interface LoginService {

    suspend fun refreshToken(refreshToken: String): LoginDTO

}
