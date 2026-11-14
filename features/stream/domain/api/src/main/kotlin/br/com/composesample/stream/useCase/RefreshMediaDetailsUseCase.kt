package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.media.MediaId
import kotlinx.coroutines.flow.Flow

interface RefreshMediaDetailsUseCase {
    operator fun invoke(id: MediaId, region: CatalogRegion): Flow<Unit>
}
