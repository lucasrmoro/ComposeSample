package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaId
import kotlinx.coroutines.flow.Flow

interface RefreshSeasonUseCase {
    operator fun invoke(id: MediaId, season: Int, region: CatalogRegion): Flow<Unit>
}
