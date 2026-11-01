package br.com.composesample.featureA.useCase

import br.com.composesample.featureA.model.SomeModel
import kotlinx.coroutines.flow.Flow

interface GetSomethingUseCase {

    suspend operator fun invoke(): Flow<List<SomeModel>>

}