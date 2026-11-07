package br.com.composesample.stream.mapper

import br.com.composesample.stream.dto.*
import br.com.composesample.stream.entity.StreamCacheEntity
import br.com.composesample.stream.model.*

interface StreamMapper {

    fun page(entity: StreamCacheEntity?, query: CatalogQuery): CatalogPage

    fun pageDTO(entity: StreamCacheEntity?): PageDTO?

    fun pageEntity(query: CatalogQuery, page: PageDTO, previous: PageDTO?): StreamCacheEntity

    fun details(entity: StreamCacheEntity?, id: MediaId, region: CatalogRegion): MediaDetails?

    fun genres(entity: StreamCacheEntity?): List<Genre>

    fun season(entity: StreamCacheEntity?): SeasonDetails?

    fun entity(key: String, value: Any): StreamCacheEntity

}
