package br.com.composesample.stream.di

import br.com.composesample.stream.dataSource.StreamRemoteDataSource
import br.com.composesample.stream.dataSource.createStreamRemoteDataSource
import br.com.composesample.stream.catalog.viewModel.CatalogViewModel
import br.com.composesample.stream.collection.viewModel.CollectionViewModel
import br.com.composesample.stream.details.viewModel.DetailsViewModel
import br.com.composesample.stream.genres.viewModel.GenresViewModel
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
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
        factory { CatalogViewModel(get(), get(), get(), get(), get()) }
        factory { CollectionViewModel(get(), get(), get(), get()) }
        factory { DetailsViewModel(get(), get(), get(), get(), get(), get()) }
        factory { GenresViewModel(get(), get(), get()) }
    }

    fun init() = loadModules(streamModule)
}
