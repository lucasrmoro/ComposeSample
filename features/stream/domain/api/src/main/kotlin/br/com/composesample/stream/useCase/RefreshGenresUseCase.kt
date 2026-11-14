package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.structural.localization.AppRegion
import kotlinx.coroutines.flow.Flow

interface RefreshGenresUseCase {
    operator fun invoke(type: MediaType, region: AppRegion): Flow<Unit>
}
