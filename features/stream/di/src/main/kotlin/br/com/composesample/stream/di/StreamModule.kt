package br.com.composesample.stream.di

import android.content.Context
import br.com.composesample.stream.analytics.StreamAnalytics
import br.com.composesample.stream.analytics.createStreamAnalytics
import br.com.composesample.stream.catalog.viewModel.CatalogViewModel
import br.com.composesample.stream.collection.viewModel.CollectionViewModel
import br.com.composesample.stream.dataSource.StreamLocalDataSource
import br.com.composesample.stream.dataSource.StreamRemoteDataSource
import br.com.composesample.stream.dataSource.createStreamLocalDataSource
import br.com.composesample.stream.dataSource.createStreamRemoteDataSource
import br.com.composesample.stream.details.viewModel.DetailsViewModel
import br.com.composesample.stream.genres.viewModel.GenresViewModel
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.mapper.StreamMapper
import br.com.composesample.stream.mapper.StreamMapperImpl
import br.com.composesample.stream.navigation.createStreamEntryPoint
import br.com.composesample.stream.repository.StreamRepository
import br.com.composesample.stream.repository.StreamRepositoryImpl
import br.com.composesample.stream.useCase.LoadNextPageUseCase
import br.com.composesample.stream.useCase.LoadNextPageUseCaseImpl
import br.com.composesample.stream.useCase.ObserveCatalogUseCase
import br.com.composesample.stream.useCase.ObserveCatalogUseCaseImpl
import br.com.composesample.stream.useCase.ObserveGenresUseCase
import br.com.composesample.stream.useCase.ObserveGenresUseCaseImpl
import br.com.composesample.stream.useCase.ObserveMediaDetailsUseCase
import br.com.composesample.stream.useCase.ObserveMediaDetailsUseCaseImpl
import br.com.composesample.stream.useCase.ObserveSeasonUseCase
import br.com.composesample.stream.useCase.ObserveSeasonUseCaseImpl
import br.com.composesample.stream.useCase.RefreshCatalogUseCase
import br.com.composesample.stream.useCase.RefreshCatalogUseCaseImpl
import br.com.composesample.stream.useCase.RefreshGenresUseCase
import br.com.composesample.stream.useCase.RefreshGenresUseCaseImpl
import br.com.composesample.stream.useCase.RefreshMediaDetailsUseCase
import br.com.composesample.stream.useCase.RefreshMediaDetailsUseCaseImpl
import br.com.composesample.stream.useCase.RefreshSeasonUseCase
import br.com.composesample.stream.useCase.RefreshSeasonUseCaseImpl
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.navigation.core.NavigationRegistry
import br.com.composesample.structural.network.http.ConfiguredHttpClientProvider
import br.com.composesample.structural.network.http.HttpClientOptions
import java.time.Clock

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
private const val AUTHORIZATION_HEADER = "Authorization"
private const val BEARER_SCHEME = "Bearer"

object StreamModule {
    fun init(context: Context, navigationRegistry: NavigationRegistry, readAccessToken: String) {
        val streamModule = dependencyModule {
            singleton<StreamAnalytics> { createStreamAnalytics(provider = get()) }
            singleton<StreamLocalDataSource> {
                createStreamLocalDataSource(
                    context = context,
                    coroutineDispatcher = dispatcherIO
                )
            }
            singleton<StreamRemoteDataSource> {
                createStreamRemoteDataSource(
                    httpClient = get<ConfiguredHttpClientProvider>().create(
                        options = HttpClientOptions(
                            baseUrl = TMDB_BASE_URL,
                            headers = readAccessToken.takeIf(String::isNotBlank)?.let {
                                mapOf(AUTHORIZATION_HEADER to "$BEARER_SCHEME $it")
                            } ?: emptyMap(),
                            useAppLanguage = true
                        )
                    ),
                    coroutineDispatcher = dispatcherIO,
                    clock = Clock.systemUTC()
                )
            }
            factory<StreamMapper> { StreamMapperImpl(jsonCodec = get()) }
            singleton<StreamRepository> {
                StreamRepositoryImpl(
                    streamLocalDataSource = get(),
                    streamRemoteDataSource = get(),
                    streamMapper = get(),
                    coroutineDispatcher = dispatcherIO
                )
            }
            factory<ObserveCatalogUseCase> {
                ObserveCatalogUseCaseImpl(streamRepository = get())
            }
            factory<RefreshCatalogUseCase> {
                RefreshCatalogUseCaseImpl(streamRepository = get())
            }
            factory<LoadNextPageUseCase> {
                LoadNextPageUseCaseImpl(streamRepository = get())
            }
            factory<ObserveGenresUseCase> {
                ObserveGenresUseCaseImpl(streamRepository = get())
            }
            factory<RefreshGenresUseCase> {
                RefreshGenresUseCaseImpl(streamRepository = get())
            }
            factory<ObserveMediaDetailsUseCase> {
                ObserveMediaDetailsUseCaseImpl(streamRepository = get())
            }
            factory<RefreshMediaDetailsUseCase> {
                RefreshMediaDetailsUseCaseImpl(streamRepository = get())
            }
            factory<ObserveSeasonUseCase> {
                ObserveSeasonUseCaseImpl(streamRepository = get())
            }
            factory<RefreshSeasonUseCase> {
                RefreshSeasonUseCaseImpl(streamRepository = get())
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
            factory { parameters ->
                CollectionViewModel(
                    type = parameters.get<MediaType>(),
                    kind = parameters.get<CollectionKind>(),
                    genreId = parameters.get<Int>().takeIf { it > 0 },
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
        loadModules(streamModule)
        navigationRegistry.register(
            entryPoint = createStreamEntryPoint(),
            isStartDestination = true
        )
    }
}
