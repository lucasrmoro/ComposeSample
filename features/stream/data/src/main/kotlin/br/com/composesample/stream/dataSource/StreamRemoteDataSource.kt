package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dto.catalog.GenresRespDTO
import br.com.composesample.stream.dto.catalog.PageRespDTO
import br.com.composesample.stream.dto.media.MediaRespDTO
import br.com.composesample.stream.dto.seasons.SeasonRespDTO
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import kotlinx.coroutines.flow.Flow

interface StreamRemoteDataSource {
    fun page(query: CatalogQuery, page: Int): Flow<PageRespDTO>
    fun details(id: MediaId): Flow<MediaRespDTO>
    fun genres(type: MediaType, languageTag: String): Flow<GenresRespDTO>
    fun season(id: MediaId, number: Int): Flow<SeasonRespDTO>
}
