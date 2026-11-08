package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaType
import br.com.composesample.stream.repository.StreamRepository

internal class ObserveGenresUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveGenresUseCase {

    override fun invoke(type: MediaType, region: CatalogRegion) =
        streamRepository.observeGenres(type, region)

}
