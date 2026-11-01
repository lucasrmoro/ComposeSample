package br.com.composesample.featureA.dataSource

import br.com.composesample.featureA.model.SomeModel
import br.com.composesample.featureA.service.FeatureAService
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import kotlinx.coroutines.flow.Flow

class FeatureARemoteDataSourceImpl(
    private val featureAService: FeatureAService
) : FeatureARemoteDataSource, BaseDataSource() {

    override suspend fun insert(someModel: SomeModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(someModel: SomeModel) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(someModel: SomeModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBy(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(page: Int, itemsPerPage: Int): Flow<List<SomeModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getBy(id: Long): SomeModel? {
        TODO("Not yet implemented")
    }

    override suspend fun getPagesCount(itemsPerPage: Int): Int {
        TODO("Not yet implemented")
    }

}