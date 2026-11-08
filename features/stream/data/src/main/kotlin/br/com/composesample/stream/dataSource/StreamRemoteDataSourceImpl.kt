package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dto.GenresDTO
import br.com.composesample.stream.dto.MediaDTO
import br.com.composesample.stream.dto.PageDTO
import br.com.composesample.stream.dto.SeasonDTO
import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.CatalogRegion
import br.com.composesample.stream.model.CollectionKind
import br.com.composesample.stream.model.MediaId
import br.com.composesample.stream.model.MediaType
import br.com.composesample.stream.query.CatalogQueryParameters
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import br.com.composesample.structural.network.http.HttpRequest
import br.com.composesample.structural.network.http.get
import java.time.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

internal class StreamRemoteDataSourceImpl(
    private val tmdbClient: TmdbClient,
    private val coroutineDispatcher: CoroutineDispatcher,
    clock: Clock
) : BaseDataSource(), StreamRemoteDataSource {

    private val queryParameters = CatalogQueryParameters(clock)

    private suspend inline fun <reified T> get(
        path: String,
        query: Map<String, String>,
        pathParameters: Map<String, String> = emptyMap()
    ): T {
        check(tmdbClient.configured) { MISSING_CREDENTIAL_MESSAGE }
        return tmdbClient.httpClient.get(
            HttpRequest(path = path, query = query, pathParameters = pathParameters)
        )
    }

    override fun details(id: MediaId, region: CatalogRegion) = call {
        val append =
            if (id.type == MediaType.MOVIE) MOVIE_DETAIL_SECTIONS else SERIES_DETAIL_SECTIONS
        get<MediaDTO>(
            if (id.type == MediaType.MOVIE) MOVIE_DETAILS_PATH else SERIES_DETAILS_PATH,
            mapOf(LANGUAGE_PARAMETER to region.languageTag, APPEND_PARAMETER to append),
            mapOf(ID_PARAMETER to id.value.toString())
        )
    }
        .flowOn(coroutineDispatcher)

    override fun genres(type: MediaType, region: CatalogRegion) = call {
        get<GenresDTO>(
            if (type == MediaType.MOVIE) MOVIE_GENRES_PATH else SERIES_GENRES_PATH,
            mapOf(LANGUAGE_PARAMETER to region.languageTag)
        )
    }
        .flowOn(coroutineDispatcher)

    override fun season(id: MediaId, number: Int, region: CatalogRegion) = call {
        get<SeasonDTO>(
            SEASON_DETAILS_PATH,
            mapOf(LANGUAGE_PARAMETER to region.languageTag),
            mapOf(ID_PARAMETER to id.value.toString(), SEASON_PARAMETER to number.toString())
        )
    }
        .flowOn(coroutineDispatcher)

    override fun page(query: CatalogQuery, page: Int) = call {
        val path =
            when (query.kind) {
                CollectionKind.POPULAR ->
                    if (query.type == MediaType.MOVIE) POPULAR_MOVIES_PATH else POPULAR_SERIES_PATH
                CollectionKind.SEARCH ->
                    if (query.type == MediaType.MOVIE) SEARCH_MOVIES_PATH else SEARCH_SERIES_PATH
                else ->
                    if (query.type == MediaType.MOVIE) DISCOVER_MOVIES_PATH
                    else DISCOVER_SERIES_PATH
            }
        get<PageDTO>(path, queryParameters.create(query, page))
    }
        .flowOn(coroutineDispatcher)

    private companion object {
        const val MOVIE_DETAILS_PATH = "movie/{id}"
        const val SERIES_DETAILS_PATH = "tv/{id}"
        const val MOVIE_GENRES_PATH = "genre/movie/list"
        const val SERIES_GENRES_PATH = "genre/tv/list"
        const val SEASON_DETAILS_PATH = "tv/{id}/season/{season}"
        const val POPULAR_MOVIES_PATH = "movie/popular"
        const val POPULAR_SERIES_PATH = "tv/popular"
        const val SEARCH_MOVIES_PATH = "search/movie"
        const val SEARCH_SERIES_PATH = "search/tv"
        const val DISCOVER_MOVIES_PATH = "discover/movie"
        const val DISCOVER_SERIES_PATH = "discover/tv"
        const val ID_PARAMETER = "id"
        const val SEASON_PARAMETER = "season"
        const val MISSING_CREDENTIAL_MESSAGE = "TMDB credential is not configured"
        const val MOVIE_DETAIL_SECTIONS = "credits,videos,release_dates,watch/providers"
        const val SERIES_DETAIL_SECTIONS = "credits,videos,content_ratings,watch/providers"
        const val LANGUAGE_PARAMETER = "language"
        const val APPEND_PARAMETER = "append_to_response"
    }
}
