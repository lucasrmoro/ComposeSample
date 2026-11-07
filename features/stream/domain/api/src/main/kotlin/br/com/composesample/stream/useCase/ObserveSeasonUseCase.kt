package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.*
import kotlinx.coroutines.flow.Flow

interface ObserveSeasonUseCase {

    operator fun invoke(id: MediaId, season: Int, region: CatalogRegion): Flow<SeasonDetails?>

}
