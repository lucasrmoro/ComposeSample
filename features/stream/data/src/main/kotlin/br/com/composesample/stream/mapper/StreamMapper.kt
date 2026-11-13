package br.com.composesample.stream.mapper

import br.com.composesample.stream.dto.catalog.PageRespDTO
import br.com.composesample.stream.entity.StreamCacheEntity
import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.details.MediaDetails
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.seasons.SeasonDetails

interface StreamMapper {
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
