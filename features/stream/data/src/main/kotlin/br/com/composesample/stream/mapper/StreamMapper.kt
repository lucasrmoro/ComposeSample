package br.com.composesample.stream.mapper

import br.com.composesample.stream.dto.PageRespDTO
import br.com.composesample.stream.entity.StreamCacheEntity
import br.com.composesample.stream.model.CatalogPage
import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.Genre
import br.com.composesample.stream.model.MediaDetails
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.SeasonDetails

internal interface StreamMapper {
    fun page(entity: StreamCacheEntity?, query: CatalogQuery): CatalogPage
    fun pageDTO(entity: StreamCacheEntity?): PageRespDTO?
    fun pageEntity(
        query: CatalogQuery,
        page: PageRespDTO,
        previous: PageRespDTO?
    ): StreamCacheEntity

    fun details(entity: StreamCacheEntity?, id: MediaId, region: CatalogRegion): MediaDetails?
    fun genres(entity: StreamCacheEntity?): List<Genre>
    fun season(entity: StreamCacheEntity?): SeasonDetails?
    fun entity(key: String, value: Any): StreamCacheEntity
}
