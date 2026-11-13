package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.repository.StreamRepository

internal class RefreshCatalogUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshCatalogUseCase {

    override fun invoke(query: CatalogQuery) = streamRepository.refreshCatalog(query)

}

fun createRefreshCatalogUseCase(streamRepository: StreamRepository): RefreshCatalogUseCase =
    RefreshCatalogUseCaseImpl(streamRepository = streamRepository)
