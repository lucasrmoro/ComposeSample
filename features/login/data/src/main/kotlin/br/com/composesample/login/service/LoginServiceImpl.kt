package br.com.composesample.login.service

import br.com.composesample.login.model.LoginDTO
import br.com.composesample.structural.network.http.HttpClient
import br.com.composesample.structural.network.http.HttpRequest
import br.com.composesample.structural.network.http.get

class LoginServiceImpl(
    private val httpClient: HttpClient
) : LoginService {

    override suspend fun refreshToken(refreshToken: String): LoginDTO =
        httpClient.get(HttpRequest("auth/refresh-token/{refreshToken}", pathParameters = mapOf("refreshToken" to refreshToken)))

}
