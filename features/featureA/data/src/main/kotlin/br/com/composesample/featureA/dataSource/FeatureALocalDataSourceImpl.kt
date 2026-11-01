package br.com.composesample.featureA.dataSource

import br.com.composesample.featureA.dto.SomeDTO
import br.com.composesample.featureA.mapper.SomeMapper
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import br.com.composesample.structural.core.ext.ONE_HUNDRED
import br.com.composesample.structural.core.ext.ZERO
import br.com.composesample.featureA.dao.SomeDao
import kotlinx.coroutines.flow.Flow

class FeatureALocalDataSourceImpl(
    private val someDao: SomeDao,
    private val someMapper: SomeMapper
) : FeatureALocalDataSource, BaseDataSource() {

    override suspend fun getSomething(): Flow<List<SomeDTO>> = call {
        someMapper.toSomeDTOList(someDao.getAll(Int.ZERO, Int.ONE_HUNDRED))
    }

}