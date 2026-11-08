package br.com.composesample.stream.repository

import br.com.composesample.stream.model.CatalogPage
import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.Genre
import br.com.composesample.stream.model.MediaDetails
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.MediaType
import br.com.composesample.stream.model.SeasonDetails
import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    fun observeCatalog(query: CatalogQuery): Flow<CatalogPage>
    fun refreshCatalog(query: CatalogQuery): Flow<Unit>
    fun loadNextPage(query: CatalogQuery): Flow<Unit>
    fun observeGenres(type: MediaType, region: CatalogRegion): Flow<List<Genre>>
    fun refreshGenres(type: MediaType, region: CatalogRegion): Flow<Unit>
    fun observeDetails(id: MediaId, region: CatalogRegion): Flow<MediaDetails?>
    fun refreshDetails(id: MediaId, region: CatalogRegion): Flow<Unit>
    fun observeSeason(id: MediaId, season: Int, region: CatalogRegion): Flow<SeasonDetails?>
    fun refreshSeason(id: MediaId, season: Int, region: CatalogRegion): Flow<Unit>
}
