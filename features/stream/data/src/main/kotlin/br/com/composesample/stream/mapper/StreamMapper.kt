package br.com.composesample.stream.mapper

import br.com.composesample.stream.dto.catalog.PageRespDTO
import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CatalogRegion
import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.details.MediaDetails
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.seasons.SeasonDetails

interface StreamMapper {
    fun page(payload: String?, query: CatalogQuery): CatalogPage
    fun pageDTO(payload: String?): PageRespDTO?
    fun pagePayload(
        query: CatalogQuery,
        page: PageRespDTO,
        previous: PageRespDTO?
    ): String

    fun details(payload: String?, id: MediaId, region: CatalogRegion): MediaDetails?
    fun genres(payload: String?): List<Genre>
    fun season(payload: String?): SeasonDetails?
    fun payload(value: Any): String
}
