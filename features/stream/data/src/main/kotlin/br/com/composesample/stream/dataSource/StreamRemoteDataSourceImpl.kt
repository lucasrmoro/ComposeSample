package br.com.composesample.stream.dataSource

import br.com.composesample.stream.dto.catalog.GenresRespDTO
import br.com.composesample.stream.dto.catalog.PageRespDTO
import br.com.composesample.stream.dto.media.MediaRespDTO
import br.com.composesample.stream.dto.seasons.SeasonRespDTO
import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.stream.dataSource.query.CatalogQueryParameters
import br.com.composesample.structural.core.base.dataSource.BaseDataSource
import br.com.composesample.structural.network.http.HttpClient
import br.com.composesample.structural.network.http.HttpRequest
import br.com.composesample.structural.network.http.get
import java.time.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

internal class StreamRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val clock: Clock
) : BaseDataSource(), StreamRemoteDataSource {

    override fun details(id: MediaId) =
        call {
            val append =
                if (id.type == MediaType.MOVIE) MOVIE_DETAIL_SECTIONS else SERIES_DETAIL_SECTIONS
            val path = if (id.type == MediaType.MOVIE) MOVIE_DETAILS_PATH else SERIES_DETAILS_PATH
            httpClient.get<MediaRespDTO>(
                HttpRequest(
                    path = path,
                    query = mapOf(APPEND_PARAMETER to append),
                    pathParameters = mapOf(ID_PARAMETER to id.value.toString())
                )
            )
        }.flowOn(coroutineDispatcher)

    override fun genres(type: MediaType, languageTag: String) =
        call {
            val path = if (type == MediaType.MOVIE) MOVIE_GENRES_PATH else SERIES_GENRES_PATH
            httpClient.get<GenresRespDTO>(
                HttpRequest(path = path, query = mapOf(LANGUAGE_PARAMETER to languageTag))
            )
        }.flowOn(coroutineDispatcher)

    override fun season(id: MediaId, number: Int) =
        call {
            httpClient.get<SeasonRespDTO>(
                HttpRequest(
                    path = SEASON_DETAILS_PATH,
                    pathParameters = mapOf(
                        ID_PARAMETER to id.value.toString(),
                        SEASON_PARAMETER to number.toString()
                    )
                )
            )
        }.flowOn(coroutineDispatcher)

    override fun page(query: CatalogQuery, page: Int) =
        call {
            val path =
                when (query.kind) {
                    CollectionKind.POPULAR ->
                        if (query.type == MediaType.MOVIE) POPULAR_MOVIES_PATH
                        else POPULAR_SERIES_PATH
                    CollectionKind.SEARCH ->
                        if (query.type == MediaType.MOVIE) SEARCH_MOVIES_PATH
                        else SEARCH_SERIES_PATH
                    else ->
                        if (query.type == MediaType.MOVIE) DISCOVER_MOVIES_PATH
                        else DISCOVER_SERIES_PATH
                }
            val params = CatalogQueryParameters(clock).create(query, page)
            httpClient.get<PageRespDTO>(HttpRequest(path = path, query = params))
        }.flowOn(coroutineDispatcher)

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
        const val MOVIE_DETAIL_SECTIONS = "credits,videos,release_dates,watch/providers"
        const val SERIES_DETAIL_SECTIONS = "credits,videos,content_ratings,watch/providers"
        const val APPEND_PARAMETER = "append_to_response"
        const val LANGUAGE_PARAMETER = "language"
    }

}
