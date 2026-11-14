package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.structural.localization.AppRegion
import kotlinx.coroutines.flow.Flow

interface RefreshMediaDetailsUseCase {
    operator fun invoke(id: MediaId, region: AppRegion): Flow<Unit>
}
