package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.SeasonDetails
import kotlinx.coroutines.flow.Flow

interface ObserveSeasonUseCase {
    operator fun invoke(id: MediaId, season: Int, region: CatalogRegion): Flow<SeasonDetails?>
}
