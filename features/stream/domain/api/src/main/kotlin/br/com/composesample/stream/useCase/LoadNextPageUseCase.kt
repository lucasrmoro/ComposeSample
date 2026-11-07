package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.*
import kotlinx.coroutines.flow.Flow

interface LoadNextPageUseCase {

    operator fun invoke(query: CatalogQuery): Flow<Unit>

}
