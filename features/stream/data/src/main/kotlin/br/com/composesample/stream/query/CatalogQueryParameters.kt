package br.com.composesample.stream.query

import br.com.composesample.stream.model.catalog.CatalogQuery
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaType
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
        addRegionParameter(params, query)
        addSearchParameter(params, query)
        addGenreParameter(params, query)
        addReleaseDateParameters(params, query)
        return params
    }

    private fun addRegionParameter(params: MutableMap<String, String>, query: CatalogQuery) {
        params[LANGUAGE_PARAMETER] = query.region.languageTag
        if (query.type == MediaType.MOVIE) params[REGION_PARAMETER] = query.region.countryCode
    }

    private fun addSearchParameter(params: MutableMap<String, String>, query: CatalogQuery) {
        if (query.kind == CollectionKind.SEARCH) params[QUERY_PARAMETER] = query.searchText.trim()
    }

    private fun addGenreParameter(params: MutableMap<String, String>, query: CatalogQuery) {
        query.genreId?.let { params[GENRE_PARAMETER] = it.toString() }
    }

    private fun addReleaseDateParameters(params: MutableMap<String, String>, query: CatalogQuery) {
        val dateRange = releaseDateRange(query) ?: return
        val dateField =
            if (query.type == MediaType.MOVIE) RELEASE_DATE_PARAMETER else AIR_DATE_PARAMETER
        params["$dateField$MINIMUM_SUFFIX"] = dateRange.start.toString()
        params["$dateField$MAXIMUM_SUFFIX"] = dateRange.end.toString()
        if (query.type == MediaType.SERIES) params[TIMEZONE_PARAMETER] = query.region.timeZoneId
        params[SORT_PARAMETER] = POPULARITY_ORDER
    }

    private fun releaseDateRange(query: CatalogQuery): DateRange? {
        val hasDateRange =
            query.kind == CollectionKind.RECENT || query.kind == CollectionKind.UPCOMING
        if (!hasDateRange) return null
        val today = LocalDate.now(clock.withZone(ZoneId.of(query.region.timeZoneId)))
        return when (query.kind) {
            CollectionKind.RECENT ->
                DateRange(start = today.minusDays(CATALOG_DATE_RANGE_DAYS), end = today)
            CollectionKind.UPCOMING ->
                DateRange(
                    start = today.plusDays(UPCOMING_START_OFFSET_DAYS),
                    end = today.plusDays(CATALOG_DATE_RANGE_DAYS)
                )
            else -> null
        }
    }

    private data class DateRange(val start: LocalDate, val end: LocalDate)

    private companion object {
        const val CATALOG_DATE_RANGE_DAYS = 30L
        const val MINIMUM_SUFFIX = ".gte"
        const val MAXIMUM_SUFFIX = ".lte"
        const val PAGE_PARAMETER = "page"
        const val INCLUDE_ADULT_PARAMETER = "include_adult"
        const val FALSE_VALUE = "false"
        const val REGION_PARAMETER = "region"
        const val LANGUAGE_PARAMETER = "language"
        const val QUERY_PARAMETER = "query"
        const val GENRE_PARAMETER = "with_genres"
        const val RELEASE_DATE_PARAMETER = "release_date"
        const val AIR_DATE_PARAMETER = "air_date"
        const val UPCOMING_START_OFFSET_DAYS = 1L
        const val TIMEZONE_PARAMETER = "timezone"
        const val SORT_PARAMETER = "sort_by"
        const val POPULARITY_ORDER = "popularity.desc"
    }

}
