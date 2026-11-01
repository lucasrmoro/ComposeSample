package br.com.composesample.login.repository

interface LoginRepository {

    suspend fun refreshToken(refreshToken: String): String?

}
