package br.com.composesample.stream.query

import br.com.composesample.stream.model.CatalogQuery
import br.com.composesample.stream.model.CollectionKind
import br.com.composesample.stream.model.MediaType
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

internal class CatalogQueryParameters(private val clock: Clock) {

    fun create(query: CatalogQuery, page: Int): Map<String, String> {
        val params =
            mutableMapOf(
                PAGE_PARAMETER to page.toString(),
                INCLUDE_ADULT_PARAMETER to FALSE_VALUE
            )
        if (query.type == MediaType.MOVIE) params[REGION_PARAMETER] = query.region.countryCode
        if (query.kind == CollectionKind.SEARCH) params[QUERY_PARAMETER] = query.searchText.trim()
        query.genreId?.let { params[GENRE_PARAMETER] = it.toString() }
        if (query.kind == CollectionKind.RECENT || query.kind == CollectionKind.UPCOMING) {
            val today = LocalDate.now(clock.withZone(ZoneId.of(query.region.timeZoneId)))
            val recent = query.kind == CollectionKind.RECENT
            val field =
                if (query.type == MediaType.MOVIE) RELEASE_DATE_PARAMETER else AIR_DATE_PARAMETER
            params["$field$MINIMUM_SUFFIX"] =
                (if (recent) today.minusDays(30) else today.plusDays(1)).toString()
            params["$field$MAXIMUM_SUFFIX"] = (if (recent) today else today.plusDays(30)).toString()
            if (query.type == MediaType.SERIES) params[TIMEZONE_PARAMETER] = query.region.timeZoneId
            params[SORT_PARAMETER] = POPULARITY_ORDER
        }
        return params
    }

    private companion object {
        const val MINIMUM_SUFFIX = ".gte"
        const val MAXIMUM_SUFFIX = ".lte"
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
