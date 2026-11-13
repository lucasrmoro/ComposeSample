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
import br.com.composesample.stream.mapper.StreamMapper
import br.com.composesample.stream.mapper.createStreamMapper
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.navigation.createStreamEntryPoint
import br.com.composesample.stream.repository.StreamRepository
import br.com.composesample.stream.repository.createStreamRepository
import br.com.composesample.stream.useCase.LoadNextPageUseCase
import br.com.composesample.stream.useCase.createLoadNextPageUseCase
import br.com.composesample.stream.useCase.ObserveCatalogUseCase
import br.com.composesample.stream.useCase.createObserveCatalogUseCase
import br.com.composesample.stream.useCase.ObserveGenresUseCase
import br.com.composesample.stream.useCase.createObserveGenresUseCase
import br.com.composesample.stream.useCase.ObserveMediaDetailsUseCase
import br.com.composesample.stream.useCase.createObserveMediaDetailsUseCase
import br.com.composesample.stream.useCase.ObserveSeasonUseCase
import br.com.composesample.stream.useCase.createObserveSeasonUseCase
import br.com.composesample.stream.useCase.RefreshCatalogUseCase
import br.com.composesample.stream.useCase.createRefreshCatalogUseCase
import br.com.composesample.stream.useCase.RefreshGenresUseCase
import br.com.composesample.stream.useCase.createRefreshGenresUseCase
import br.com.composesample.stream.useCase.RefreshMediaDetailsUseCase
import br.com.composesample.stream.useCase.createRefreshMediaDetailsUseCase
import br.com.composesample.stream.useCase.RefreshSeasonUseCase
import br.com.composesample.stream.useCase.createRefreshSeasonUseCase
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
        val dataModule = dependencyModule {
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
            factory<StreamMapper> { createStreamMapper(jsonCodec = get()) }
            singleton<StreamRepository> {
                createStreamRepository(
                    streamLocalDataSource = get(),
                    streamRemoteDataSource = get(),
                    streamMapper = get(),
                    coroutineDispatcher = dispatcherIO
                )
            }
        }
        val domainModule = dependencyModule {
            factory<ObserveCatalogUseCase> {
                createObserveCatalogUseCase(streamRepository = get())
            }
            factory<RefreshCatalogUseCase> {
                createRefreshCatalogUseCase(streamRepository = get())
            }
            factory<LoadNextPageUseCase> {
                createLoadNextPageUseCase(streamRepository = get())
            }
            factory<ObserveGenresUseCase> {
                createObserveGenresUseCase(streamRepository = get())
            }
            factory<RefreshGenresUseCase> {
                createRefreshGenresUseCase(streamRepository = get())
            }
            factory<ObserveMediaDetailsUseCase> {
                createObserveMediaDetailsUseCase(streamRepository = get())
            }
            factory<RefreshMediaDetailsUseCase> {
                createRefreshMediaDetailsUseCase(streamRepository = get())
            }
            factory<ObserveSeasonUseCase> {
                createObserveSeasonUseCase(streamRepository = get())
            }
            factory<RefreshSeasonUseCase> {
                createRefreshSeasonUseCase(streamRepository = get())
            }
        }
        val presentationModule = dependencyModule {
            singleton<StreamAnalytics> { createStreamAnalytics(provider = get()) }
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
        loadModules(dataModule, domainModule, presentationModule)
        navigationRegistry.register(
            entryPoint = createStreamEntryPoint(),
            isStartDestination = true
        )
    }
}
