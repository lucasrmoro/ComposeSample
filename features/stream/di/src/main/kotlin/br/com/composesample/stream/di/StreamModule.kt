package br.com.composesample.stream.di

import br.com.composesample.stream.dataSource.StreamRemoteDataSource
import br.com.composesample.stream.dataSource.createStreamRemoteDataSource
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.network.ext.httpClientNoAuth
import java.time.Clock

object StreamModule {
    private val streamModule = dependencyModule {
        factory<StreamRemoteDataSource> {
            createStreamRemoteDataSource(
                httpClient = httpClientNoAuth,
                coroutineDispatcher = dispatcherIO,
                clock = Clock.systemUTC()
            )
        }
    }

    fun init() = loadModules(streamModule)
}
