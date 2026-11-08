package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dto.GenresRespDTO
import br.com.composesample.stream.dto.MediaRespDTO
import br.com.composesample.stream.dto.PageRespDTO
import br.com.composesample.stream.dto.SeasonRespDTO
import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.MediaType
import kotlinx.coroutines.flow.Flow

interface StreamRemoteDataSource {
    fun page(query: CatalogQuery, page: Int): Flow<PageRespDTO>
    fun details(id: MediaId): Flow<MediaRespDTO>
    fun genres(type: MediaType): Flow<GenresRespDTO>
    fun season(id: MediaId, number: Int): Flow<SeasonRespDTO>
}
