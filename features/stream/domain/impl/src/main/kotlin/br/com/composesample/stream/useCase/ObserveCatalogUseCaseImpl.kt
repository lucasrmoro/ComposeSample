package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.*
import br.com.composesample.stream.repository.StreamRepository

class ObserveCatalogUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveCatalogUseCase {

    override fun invoke(query: CatalogQuery) = streamRepository.observeCatalog(query)

}
