package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.*
import kotlinx.coroutines.flow.Flow

interface ObserveGenresUseCase {
    operator fun invoke(type: MediaType, region: CatalogRegion): Flow<List<Genre>>
}
