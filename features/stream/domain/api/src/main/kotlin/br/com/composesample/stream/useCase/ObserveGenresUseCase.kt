package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.media.MediaType
import kotlinx.coroutines.flow.Flow

interface ObserveGenresUseCase {
    operator fun invoke(type: MediaType, region: CatalogRegion): Flow<List<Genre>>
}
