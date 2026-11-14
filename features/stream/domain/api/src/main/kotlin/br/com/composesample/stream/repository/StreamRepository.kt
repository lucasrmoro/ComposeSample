package br.com.composesample.stream.repository

import br.com.composesample.stream.model.catalog.CatalogPage
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.Genre
import br.com.composesample.stream.model.details.MediaDetails
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.model.seasons.SeasonDetails
import br.com.composesample.structural.localization.AppRegion
import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    fun observeCatalog(query: CatalogQuery): Flow<CatalogPage>
    fun refreshCatalog(query: CatalogQuery): Flow<Unit>
    fun loadNextPage(query: CatalogQuery): Flow<Unit>
    fun observeGenres(type: MediaType, region: AppRegion): Flow<List<Genre>>
    fun refreshGenres(type: MediaType, region: AppRegion): Flow<Unit>
    fun observeDetails(id: MediaId, region: AppRegion): Flow<MediaDetails?>
    fun refreshDetails(id: MediaId, region: AppRegion): Flow<Unit>
    fun observeSeason(id: MediaId, season: Int, region: AppRegion): Flow<SeasonDetails?>
    fun refreshSeason(id: MediaId, season: Int, region: AppRegion): Flow<Unit>
}
