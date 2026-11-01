package br.com.composesample.featureA.dataSource

import br.com.composesample.featureA.model.SomeModel
import kotlinx.coroutines.flow.Flow

interface FeatureARemoteDataSource {

    suspend fun insert(someModel: SomeModel): Long
    suspend fun update(someModel: SomeModel)
    suspend fun delete(someModel: SomeModel)
    suspend fun deleteBy(id: Long)
    suspend fun getAll(page: Int, itemsPerPage: Int): Flow<List<SomeModel>>
    suspend fun getBy(id: Long): SomeModel?
    suspend fun getPagesCount(itemsPerPage: Int): Int

}