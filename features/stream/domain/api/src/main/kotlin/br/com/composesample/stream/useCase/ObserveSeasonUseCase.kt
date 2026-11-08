package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.seasons.SeasonDetails
import kotlinx.coroutines.flow.Flow

interface ObserveSeasonUseCase {
    operator fun invoke(id: MediaId, season: Int, region: CatalogRegion): Flow<SeasonDetails?>
}
