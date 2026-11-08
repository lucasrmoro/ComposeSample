package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.Genre
import br.com.composesample.stream.model.MediaType
import kotlinx.coroutines.flow.Flow

interface ObserveGenresUseCase {
    operator fun invoke(type: MediaType, region: CatalogRegion): Flow<List<Genre>>
}
