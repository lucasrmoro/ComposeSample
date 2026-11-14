package br.com.composesample.stream.dataSource

import kotlinx.coroutines.flow.Flow

interface StreamLocalDataSource {
    fun observe(key: String): Flow<String?>
    fun save(key: String, payload: String): Flow<Unit>
}
