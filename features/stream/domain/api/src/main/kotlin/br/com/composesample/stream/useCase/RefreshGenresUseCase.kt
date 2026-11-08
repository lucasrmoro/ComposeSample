package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaType
import kotlinx.coroutines.flow.Flow

interface RefreshGenresUseCase {
    operator fun invoke(type: MediaType, region: CatalogRegion): Flow<Unit>
}
