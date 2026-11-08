package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.repository.StreamRepository

internal class RefreshSeasonUseCaseImpl(private val streamRepository: StreamRepository) :
    RefreshSeasonUseCase {

    override fun invoke(id: MediaId, season: Int, region: CatalogRegion) =
        streamRepository.refreshSeason(id, season, region)

}
