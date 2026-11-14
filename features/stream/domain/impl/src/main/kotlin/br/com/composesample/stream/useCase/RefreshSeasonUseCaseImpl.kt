package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.repository.StreamRepository

class RefreshSeasonUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshSeasonUseCase {

    override fun invoke(id: MediaId, season: Int, region: CatalogRegion) =
        streamRepository.refreshSeason(id, season, region)

}
