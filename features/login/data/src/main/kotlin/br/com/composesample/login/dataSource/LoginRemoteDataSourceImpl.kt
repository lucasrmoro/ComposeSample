package br.com.composesample.login.dataSource

import br.com.composesample.login.dataSource.service.LoginService
import br.com.composesample.login.dto.LoginDTO
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import kotlinx.coroutines.flow.Flow

class LoginRemoteDataSourceImpl(
    private val loginService: LoginService
) : BaseDataSource(), LoginRemoteDataSource {

    override fun refreshToken(refreshToken: String): Flow<LoginDTO> = call { loginService.refreshToken(refreshToken) }

}
