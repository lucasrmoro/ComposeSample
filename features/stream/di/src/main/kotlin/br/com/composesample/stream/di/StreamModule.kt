package br.com.composesample.stream.di

import br.com.composesample.stream.catalog.viewModel.CatalogViewModel
import br.com.composesample.stream.collection.viewModel.CollectionViewModel
import br.com.composesample.stream.dataSource.StreamRemoteDataSource
import br.com.composesample.stream.dataSource.createStreamRemoteDataSource
import br.com.composesample.stream.details.viewModel.DetailsViewModel
import br.com.composesample.stream.genres.viewModel.GenresViewModel
import br.com.composesample.stream.model.media.MediaId
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
        factory {
            CatalogViewModel(
                observeCatalogUseCase = get(),
                refreshCatalogUseCase = get(),
                loadNextPageUseCase = get(),
                appRegionProvider = get(),
                streamAnalytics = get()
            )
        }
        factory {
            CollectionViewModel(
                observeCatalogUseCase = get(),
                refreshCatalogUseCase = get(),
                loadNextPageUseCase = get(),
                appRegionProvider = get()
            )
        }
        factory { parameters ->
            DetailsViewModel(
                mediaId = parameters.get<MediaId>(),
                observeMediaDetailsUseCase = get(),
                refreshMediaDetailsUseCase = get(),
                observeSeasonUseCase = get(),
                refreshSeasonUseCase = get(),
                appRegionProvider = get(),
                networkConnectivityProvider = get()
            )
        }
        factory {
            GenresViewModel(
                observeGenresUseCase = get(),
                refreshGenresUseCase = get(),
                appRegionProvider = get()
            )
        }
    }

    fun init() = loadModules(streamModule)
}
