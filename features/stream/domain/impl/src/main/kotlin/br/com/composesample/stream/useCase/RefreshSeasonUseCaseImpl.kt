package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.repository.StreamRepository
import br.com.composesample.structural.localization.AppRegion

class RefreshSeasonUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshSeasonUseCase {

    override fun invoke(id: MediaId, season: Int, region: AppRegion) =
        streamRepository.refreshSeason(id, season, region)

}
