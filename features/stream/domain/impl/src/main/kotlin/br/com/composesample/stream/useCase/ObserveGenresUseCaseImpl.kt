package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.*
import br.com.composesample.stream.repository.StreamRepository

class ObserveGenresUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveGenresUseCase {

    override fun invoke(type: MediaType, region: CatalogRegion) =
        streamRepository.observeGenres(type, region)

}
