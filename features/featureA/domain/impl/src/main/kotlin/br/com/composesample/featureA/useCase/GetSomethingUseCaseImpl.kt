package br.com.composesample.featureA.useCase

import br.com.composesample.featureA.model.SomeModel
import br.com.composesample.featureA.repository.FeatureARepository
import br.com.composesample.structural.core.ext.ONE_HUNDRED
import br.com.composesample.structural.core.ext.ZERO
import kotlinx.coroutines.flow.Flow

class GetSomethingUseCaseImpl(
    private val featureARepository: FeatureARepository
) : GetSomethingUseCase {

    override suspend fun invoke(): Flow<List<SomeModel>> =
        featureARepository.getAll(page = Int.ZERO, itemsPerPage = Int.ONE_HUNDRED)

}