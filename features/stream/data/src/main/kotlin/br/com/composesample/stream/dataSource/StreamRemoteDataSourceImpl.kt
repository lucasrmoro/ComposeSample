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
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import br.com.composesample.structural.network.http.HttpRequest
import br.com.composesample.structural.network.http.get
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

internal class StreamRemoteDataSourceImpl(
    private val tmdbClient: TmdbClient,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val clock: Clock
) : BaseDataSource(), StreamRemoteDataSource {

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
        val params =
            mutableMapOf(
                LANGUAGE_PARAMETER to query.region.languageTag,
                PAGE_PARAMETER to page.toString(),
                INCLUDE_ADULT_PARAMETER to FALSE_VALUE
            )
        val today = LocalDate.now(clock.withZone(ZoneId.of(query.region.timeZoneId)))
        if (query.type == MediaType.MOVIE) params[REGION_PARAMETER] = query.region.countryCode
        if (query.kind == CollectionKind.SEARCH) params[QUERY_PARAMETER] = query.searchText.trim()
        query.genreId?.let { params[GENRE_PARAMETER] = it.toString() }
        if (query.kind == CollectionKind.RECENT || query.kind == CollectionKind.UPCOMING) {
            val recent = query.kind == CollectionKind.RECENT
            val field =
                if (query.type == MediaType.MOVIE) RELEASE_DATE_PARAMETER else AIR_DATE_PARAMETER
            params["$field$MINIMUM_SUFFIX"] =
                (if (recent) today.minusDays(30) else today.plusDays(1)).toString()
            params["$field$MAXIMUM_SUFFIX"] = (if (recent) today else today.plusDays(30)).toString()
            if (query.type == MediaType.SERIES) params[TIMEZONE_PARAMETER] = query.region.timeZoneId
            params[SORT_PARAMETER] = POPULARITY_ORDER
        }
        get<PageDTO>(path, params)
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
        const val MINIMUM_SUFFIX = ".gte"
        const val MAXIMUM_SUFFIX = ".lte"
        const val MISSING_CREDENTIAL_MESSAGE = "TMDB credential is not configured"
        const val MOVIE_DETAIL_SECTIONS = "credits,videos,release_dates,watch/providers"
        const val SERIES_DETAIL_SECTIONS = "credits,videos,content_ratings,watch/providers"
        const val LANGUAGE_PARAMETER = "language"
        const val APPEND_PARAMETER = "append_to_response"
        const val PAGE_PARAMETER = "page"
        const val INCLUDE_ADULT_PARAMETER = "include_adult"
        const val FALSE_VALUE = "false"
        const val REGION_PARAMETER = "region"
        const val QUERY_PARAMETER = "query"
        const val GENRE_PARAMETER = "with_genres"
        const val RELEASE_DATE_PARAMETER = "release_date"
        const val AIR_DATE_PARAMETER = "air_date"
        const val TIMEZONE_PARAMETER = "timezone"
        const val SORT_PARAMETER = "sort_by"
        const val POPULARITY_ORDER = "popularity.desc"
    }
}
