package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.CatalogPage
import br.com.composesample.stream.model.CatalogQuery
import kotlinx.coroutines.flow.Flow

interface ObserveCatalogUseCase {
    operator fun invoke(query: CatalogQuery): Flow<CatalogPage>
}
