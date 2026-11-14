package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.structural.localization.AppRegion
import kotlinx.coroutines.flow.Flow

interface RefreshSeasonUseCase {
    operator fun invoke(id: MediaId, season: Int, region: AppRegion): Flow<Unit>
}
