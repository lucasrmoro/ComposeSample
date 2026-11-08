package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dto.GenresDTO
import br.com.composesample.stream.dto.MediaDTO
import br.com.composesample.stream.dto.PageDTO
import br.com.composesample.stream.dto.SeasonDTO
import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.MediaType
import kotlinx.coroutines.flow.Flow

internal interface StreamRemoteDataSource {
    fun page(query: CatalogQuery, page: Int): Flow<PageDTO>
    fun details(id: MediaId, region: CatalogRegion): Flow<MediaDTO>
    fun genres(type: MediaType, region: CatalogRegion): Flow<GenresDTO>
    fun season(id: MediaId, number: Int, region: CatalogRegion): Flow<SeasonDTO>
}
