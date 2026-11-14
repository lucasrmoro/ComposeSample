package br.com.composesample.structural.assets_cache

import br.com.composesample.structural.core.concurrency.KeyedMutex
import java.time.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class AssetsCacheProviderImpl(
    private val assetsCacheStore: AssetsCacheStore,
    private val assetCacheWriter: AssetCacheWriter,
    private val assetsCacheConfiguration: AssetsCacheConfiguration,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : AssetsCacheProvider {

    private val urlMutex = KeyedMutex<String>()
    private val storeMutex = Mutex()

    override fun observe(request: AssetRequest, previousUrl: String?): Flow<AssetState> = flow {
        urlMutex.withLock(request.url) { load(request, previousUrl) }
    }.flowOn(coroutineDispatcher)

    private suspend fun FlowCollector<AssetState>.load(
        request: AssetRequest,
        previousUrl: String?
    ) {
        val url = request.url
        val current = storeMutex.withLock { assetsCacheStore.find(url = url) }
        val fallback = current?.file ?: previousUrl?.let { previous ->
            storeMutex.withLock { assetsCacheStore.find(url = previous) }?.file
        }
        val now = clock.millis()
        val needsRefresh =
            current?.let {
                now - it.downloadedAt >= assetsCacheConfiguration.maximumAgeMillis
            } ?: true

        val accessUpdate = storeMutex.withLock { assetsCacheStore.touch(url = url, now = now) }
        fallback?.let {
            emit(
                AssetState(file = it, isRefreshing = needsRefresh, failed = accessUpdate.isFailure)
            )
        }
        if (!needsRefresh) return

        emitAll(
            flow {
                val file = assetCacheWriter.download(request)
                val refreshed =
                    storeMutex.withLock {
                        assetsCacheStore.save(url = url, file = file, now = clock.millis())
                    }
                emit(AssetState(file = refreshed, isRefreshing = false, failed = false))
            }.catch {
                emit(AssetState(file = fallback, isRefreshing = false, failed = true))
            }
        )
    }

}
