package br.com.composesample.stream.repository

import br.com.composesample.structural.core.concurrency.KeyedMutex
import br.com.composesample.stream.dataSource.StreamLocalDataSource
import br.com.composesample.stream.dataSource.StreamRemoteDataSource
import br.com.composesample.stream.mapper.StreamMapper
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

internal class StreamRepositoryImpl(
    private val streamLocalDataSource: StreamLocalDataSource,
    private val streamRemoteDataSource: StreamRemoteDataSource,
    private val streamMapper: StreamMapper,
    private val coroutineDispatcher: CoroutineDispatcher
) : StreamRepository {

    private val catalogLocks = KeyedMutex<String>()

    private fun detailsKey(id: MediaId, region: CatalogRegion) =
        "details:${id.type}:${id.value}:${region.languageTag}:${region.countryCode}"

    private fun genreKey(type: MediaType, region: CatalogRegion) =
        "genres:$type:${region.languageTag}"

    private fun seasonKey(id: MediaId, season: Int, region: CatalogRegion) =
        "season:${detailsKey(id, region)}:$season"

    override fun observeCatalog(query: CatalogQuery) =
        streamLocalDataSource
            .observe(query.key)
            .map { streamMapper.page(it, query) }
            .distinctUntilChanged()
            .flowOn(coroutineDispatcher)

    override fun refreshCatalog(query: CatalogQuery) = savePage(query, replace = true)

    override fun loadNextPage(query: CatalogQuery) = savePage(query, replace = false)

    private fun savePage(query: CatalogQuery, replace: Boolean) = flow {
        catalogLocks.withLock(query.key) {
            val previous =
                if (replace) null
                else streamMapper.pageDTO(streamLocalDataSource.observe(query.key).first())
            if (previous != null && previous.page >= previous.totalPages) {
                emit(Unit)
                return@withLock
            }
            val next = (previous?.page ?: 0) + 1
            val page = streamRemoteDataSource.page(query, next).single()
            val payload = streamMapper.pagePayload(query, page, previous)
            streamLocalDataSource.save(key = query.key, payload = payload).single()
            emit(Unit)
        }
    }.flowOn(coroutineDispatcher)

    override fun observeGenres(type: MediaType, region: CatalogRegion) =
        streamLocalDataSource
            .observe(genreKey(type, region))
            .map(streamMapper::genres)
            .distinctUntilChanged()
            .flowOn(coroutineDispatcher)

    override fun refreshGenres(type: MediaType, region: CatalogRegion) = flow {
        streamLocalDataSource
            .save(
                key = genreKey(type, region),
                payload = streamMapper.payload(streamRemoteDataSource.genres(type).single())
            )
            .single()
        emit(Unit)
    }.flowOn(coroutineDispatcher)

    override fun observeDetails(id: MediaId, region: CatalogRegion) =
        streamLocalDataSource
            .observe(detailsKey(id, region))
            .map { streamMapper.details(it, id, region) }
            .distinctUntilChanged()
            .flowOn(coroutineDispatcher)

    override fun refreshDetails(id: MediaId, region: CatalogRegion) = flow {
        streamLocalDataSource
            .save(
                key = detailsKey(id, region),
                payload = streamMapper.payload(streamRemoteDataSource.details(id).single())
            )
            .single()
        emit(Unit)
    }.flowOn(coroutineDispatcher)

    override fun observeSeason(id: MediaId, season: Int, region: CatalogRegion) =
        streamLocalDataSource
            .observe(seasonKey(id, season, region))
            .map(streamMapper::season)
            .distinctUntilChanged()
            .flowOn(coroutineDispatcher)

    override fun refreshSeason(id: MediaId, season: Int, region: CatalogRegion) = flow {
        streamLocalDataSource
            .save(
                key = seasonKey(id, season, region),
                payload = streamMapper.payload(streamRemoteDataSource.season(id, season).single())
            )
            .single()
        emit(Unit)
    }.flowOn(coroutineDispatcher)

}

fun createStreamRepository(
    streamLocalDataSource: StreamLocalDataSource,
    streamRemoteDataSource: StreamRemoteDataSource,
    streamMapper: StreamMapper,
    coroutineDispatcher: CoroutineDispatcher
): StreamRepository =
    StreamRepositoryImpl(
        streamLocalDataSource = streamLocalDataSource,
        streamRemoteDataSource = streamRemoteDataSource,
        streamMapper = streamMapper,
        coroutineDispatcher = coroutineDispatcher
    )
