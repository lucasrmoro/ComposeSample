package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaDetails
import br.com.composesample.stream.model.MediaId
import kotlinx.coroutines.flow.Flow

interface ObserveMediaDetailsUseCase {
    operator fun invoke(id: MediaId, region: CatalogRegion): Flow<MediaDetails?>
}
