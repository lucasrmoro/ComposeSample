package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.repository.StreamRepository

internal class ObserveSeasonUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveSeasonUseCase {

    override fun invoke(id: MediaId, season: Int, region: CatalogRegion) =
        streamRepository.observeSeason(id, season, region)

}
