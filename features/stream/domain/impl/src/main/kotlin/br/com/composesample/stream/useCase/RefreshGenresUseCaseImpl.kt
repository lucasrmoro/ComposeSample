package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.repository.StreamRepository

internal class RefreshGenresUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshGenresUseCase {

    override fun invoke(type: MediaType, region: CatalogRegion) =
        streamRepository.refreshGenres(type, region)

}
