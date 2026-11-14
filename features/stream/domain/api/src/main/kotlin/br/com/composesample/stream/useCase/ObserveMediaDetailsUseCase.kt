package br.com.composesample.stream.useCase

import br.com.composesample.stream.model.details.MediaDetails
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.structural.localization.AppRegion
import kotlinx.coroutines.flow.Flow

interface ObserveMediaDetailsUseCase {
    operator fun invoke(id: MediaId, region: AppRegion): Flow<MediaDetails?>
}
