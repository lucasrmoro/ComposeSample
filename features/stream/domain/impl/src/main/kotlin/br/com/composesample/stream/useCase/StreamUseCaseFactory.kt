package br.com.composesample.stream.useCase

import br.com.composesample.stream.repository.StreamRepository

fun createLoadNextPageUseCase(streamRepository: StreamRepository): LoadNextPageUseCase =
    LoadNextPageUseCaseImpl(streamRepository = streamRepository)

fun createObserveCatalogUseCase(streamRepository: StreamRepository): ObserveCatalogUseCase =
    ObserveCatalogUseCaseImpl(streamRepository = streamRepository)

fun createObserveGenresUseCase(streamRepository: StreamRepository): ObserveGenresUseCase =
    ObserveGenresUseCaseImpl(streamRepository = streamRepository)

fun createObserveMediaDetailsUseCase(
    streamRepository: StreamRepository
): ObserveMediaDetailsUseCase =
    ObserveMediaDetailsUseCaseImpl(streamRepository = streamRepository)

fun createObserveSeasonUseCase(streamRepository: StreamRepository): ObserveSeasonUseCase =
    ObserveSeasonUseCaseImpl(streamRepository = streamRepository)

fun createRefreshCatalogUseCase(streamRepository: StreamRepository): RefreshCatalogUseCase =
    RefreshCatalogUseCaseImpl(streamRepository = streamRepository)

fun createRefreshGenresUseCase(streamRepository: StreamRepository): RefreshGenresUseCase =
    RefreshGenresUseCaseImpl(streamRepository = streamRepository)

fun createRefreshMediaDetailsUseCase(
    streamRepository: StreamRepository
): RefreshMediaDetailsUseCase =
    RefreshMediaDetailsUseCaseImpl(streamRepository = streamRepository)

fun createRefreshSeasonUseCase(streamRepository: StreamRepository): RefreshSeasonUseCase =
    RefreshSeasonUseCaseImpl(streamRepository = streamRepository)
