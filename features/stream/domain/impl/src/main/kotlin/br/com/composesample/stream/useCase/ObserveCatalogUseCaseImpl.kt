package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.repository.StreamRepository

internal class ObserveCatalogUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveCatalogUseCase {

    override fun invoke(query: CatalogQuery) = streamRepository.observeCatalog(query)

}

fun createObserveCatalogUseCase(streamRepository: StreamRepository): ObserveCatalogUseCase =
    ObserveCatalogUseCaseImpl(streamRepository = streamRepository)
