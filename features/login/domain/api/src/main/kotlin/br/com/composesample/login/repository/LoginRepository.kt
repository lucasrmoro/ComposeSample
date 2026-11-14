package br.com.composesample.login.repository

import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun refreshToken(refreshToken: String): Flow<String?>

}
