package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.repository.StreamRepository
import br.com.composesample.structural.localization.AppRegion

class ObserveGenresUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveGenresUseCase {

    override fun invoke(type: MediaType, region: AppRegion) =
        streamRepository.observeGenres(type, region)

}
