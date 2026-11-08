package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.repository.StreamRepository

internal class RefreshMediaDetailsUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshMediaDetailsUseCase {

    override fun invoke(id: MediaId, region: CatalogRegion) =
        streamRepository.refreshDetails(id, region)

}
