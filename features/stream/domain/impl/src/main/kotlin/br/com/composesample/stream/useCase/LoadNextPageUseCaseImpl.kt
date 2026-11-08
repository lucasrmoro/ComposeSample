package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.repository.StreamRepository

internal class LoadNextPageUseCaseImpl(private val streamRepository: StreamRepository) :
    LoadNextPageUseCase {

    override fun invoke(query: CatalogQuery) = streamRepository.loadNextPage(query)

}
