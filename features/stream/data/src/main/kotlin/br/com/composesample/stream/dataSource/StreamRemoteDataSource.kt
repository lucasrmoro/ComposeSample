package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dto.*
import br.com.composesample.stream.model.*
import kotlinx.coroutines.flow.Flow

interface StreamRemoteDataSource {
    fun page(query: CatalogQuery, page: Int): Flow<PageDTO>
    fun details(id: MediaId, region: CatalogRegion): Flow<MediaDTO>
    fun genres(type: MediaType, region: CatalogRegion): Flow<GenresDTO>
    fun season(id: MediaId, number: Int, region: CatalogRegion): Flow<SeasonDTO>
}
