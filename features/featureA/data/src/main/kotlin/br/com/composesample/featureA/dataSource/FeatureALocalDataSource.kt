package br.com.composesample.featureA.dataSource

import br.com.composesample.featureA.dto.SomeDTO
import kotlinx.coroutines.flow.Flow

interface FeatureALocalDataSource {

    suspend fun getSomething(): Flow<List<SomeDTO>>

}