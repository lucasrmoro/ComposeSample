package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.*
import br.com.composesample.stream.repository.StreamRepository

class ObserveMediaDetailsUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveMediaDetailsUseCase {

    override fun invoke(id: MediaId, region: CatalogRegion) =
        streamRepository.observeDetails(id, region)

}
