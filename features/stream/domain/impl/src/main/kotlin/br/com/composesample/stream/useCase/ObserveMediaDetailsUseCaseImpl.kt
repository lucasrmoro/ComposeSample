package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.repository.StreamRepository
import br.com.composesample.structural.localization.AppRegion

class ObserveMediaDetailsUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveMediaDetailsUseCase {

    override fun invoke(id: MediaId, region: AppRegion) =
        streamRepository.observeDetails(id, region)

}
