package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.repository.StreamRepository
import br.com.composesample.structural.localization.AppRegion

class RefreshMediaDetailsUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshMediaDetailsUseCase {

    override fun invoke(id: MediaId, region: AppRegion) =
        streamRepository.refreshDetails(id, region)

}
