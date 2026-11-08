package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogQuery
import kotlinx.coroutines.flow.Flow

interface RefreshCatalogUseCase {
    operator fun invoke(query: CatalogQuery): Flow<Unit>
}
