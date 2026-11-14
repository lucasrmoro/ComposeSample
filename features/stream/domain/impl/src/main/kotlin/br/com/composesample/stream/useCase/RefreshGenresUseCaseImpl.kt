package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.repository.StreamRepository
import br.com.composesample.structural.localization.AppRegion

class RefreshGenresUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshGenresUseCase {

    override fun invoke(type: MediaType, region: AppRegion) =
        streamRepository.refreshGenres(type, region)

}
