package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.catalog.CatalogQuery
import kotlinx.coroutines.flow.Flow

interface LoadNextPageUseCase {
    operator fun invoke(query: CatalogQuery): Flow<Unit>
}
