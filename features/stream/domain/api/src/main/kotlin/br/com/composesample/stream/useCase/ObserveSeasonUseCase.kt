package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.seasons.SeasonDetails
import br.com.composesample.structural.localization.AppRegion
import kotlinx.coroutines.flow.Flow

interface ObserveSeasonUseCase {
    operator fun invoke(id: MediaId, season: Int, region: AppRegion): Flow<SeasonDetails?>
}
