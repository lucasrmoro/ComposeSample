package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.repository.StreamRepository

internal class ObserveSeasonUseCaseImpl(private val streamRepository: StreamRepository) :
    ObserveSeasonUseCase {

    override fun invoke(id: MediaId, season: Int, region: CatalogRegion) =
        streamRepository.observeSeason(id, season, region)

}

fun createObserveSeasonUseCase(streamRepository: StreamRepository): ObserveSeasonUseCase =
    ObserveSeasonUseCaseImpl(streamRepository = streamRepository)
