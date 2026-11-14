package br.com.composesample.login.dataSource

import br.com.composesample.login.dto.LoginDTO
import kotlinx.coroutines.flow.Flow

interface LoginRemoteDataSource {

    fun refreshToken(refreshToken: String): Flow<LoginDTO>

}
