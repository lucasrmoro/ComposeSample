package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.repository.StreamRepository

class RefreshMediaDetailsUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshMediaDetailsUseCase {

    override fun invoke(id: MediaId, region: CatalogRegion) =
        streamRepository.refreshDetails(id, region)

}
