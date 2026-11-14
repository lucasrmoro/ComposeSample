package br.com.composesample.login

import br.com.composesample.login.analytics.LoginAnalytics
import br.com.composesample.login.analytics.createLoginAnalytics
import br.com.composesample.login.dataSource.LoginAccessTokenRefresher
import br.com.composesample.login.dataSource.LoginRemoteDataSource
import br.com.composesample.login.dataSource.LoginRemoteDataSourceImpl
import br.com.composesample.login.dataSource.service.LoginService
import br.com.composesample.login.dataSource.service.LoginServiceImpl
import br.com.composesample.login.repository.LoginRepository
import br.com.composesample.login.repository.LoginRepositoryImpl
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.network.AccessTokenRefresher
import br.com.composesample.structural.network.ext.httpClientNoAuth

object LoginModule {

    private val dataModule = dependencyModule {
        singleton<LoginAnalytics> { createLoginAnalytics(provider = get()) }
        factory<LoginService> { LoginServiceImpl(httpClient = httpClientNoAuth) }
        factory<LoginRemoteDataSource> { LoginRemoteDataSourceImpl(loginService = get()) }
        factory<LoginRepository> { LoginRepositoryImpl(loginRemoteDataSource = get(), analytics = get()) }
        factory<AccessTokenRefresher> { LoginAccessTokenRefresher(loginRepository = get()) }
    }

    fun init() {
        loadModules(
            dataModule
        )
    }

}
