package br.com.composesample.stream.model.catalog

import br.com.composesample.stream.model.media.MediaType

data class CatalogQuery(
    val type: MediaType,
    val kind: CollectionKind,
    val region: CatalogRegion,
    val genreId: Int? = null,
    val searchText: String = ""
) {

    val key: String
        get() =
            listOf(
                    type.name,
                    kind.name,
                    region.languageTag,
                    region.countryCode,
                    region.timeZoneId,
                    genreId?.toString().orEmpty(),
                    searchText.trim().lowercase(java.util.Locale.ROOT)
                )
                .joinToString("|") { "${it.length}:$it" }

}
