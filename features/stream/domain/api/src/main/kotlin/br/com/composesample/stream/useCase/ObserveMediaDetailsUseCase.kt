package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.*
import kotlinx.coroutines.flow.Flow

interface ObserveMediaDetailsUseCase {
    operator fun invoke(id: MediaId, region: CatalogRegion): Flow<MediaDetails?>
}
