package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.repository.StreamRepository

internal class ObserveMediaDetailsUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveMediaDetailsUseCase {

    override fun invoke(id: MediaId, region: CatalogRegion) =
        streamRepository.observeDetails(id, region)

}
