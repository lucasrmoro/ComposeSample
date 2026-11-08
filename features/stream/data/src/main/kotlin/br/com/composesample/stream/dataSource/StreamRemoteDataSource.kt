package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dto.GenresDTO
import br.com.composesample.stream.dto.MediaDTO
import br.com.composesample.stream.dto.PageDTO
import br.com.composesample.stream.dto.SeasonDTO
import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.MediaType
import kotlinx.coroutines.flow.Flow

internal interface StreamRemoteDataSource {
    fun page(query: CatalogQuery, page: Int): Flow<PageDTO>
    fun details(id: MediaId): Flow<MediaDTO>
    fun genres(type: MediaType): Flow<GenresDTO>
    fun season(id: MediaId, number: Int): Flow<SeasonDTO>
}
