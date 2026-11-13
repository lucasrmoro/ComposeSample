package br.com.composesample.stream.navigation

import androidx.navigation.NavController
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.structural.navigation.core.encodeRouteValue

internal object StreamRoutes {

    const val CATALOG = "catalog"
    const val GENRES = "genres/{type}"
    const val COLLECTION = "collection/{type}/{kind}/{genre}?title={title}"
    const val DETAILS = "details/{type}/{id}"

    object Arguments {
        const val TYPE = "type"
        const val KIND = "kind"
        const val GENRE = "genre"
        const val TITLE = "title"
        const val ID = "id"
    }

    fun genres(type: MediaType) = "genres/${type.name}"

    fun collection(
        type: MediaType,
        kind: CollectionKind,
        genreId: Int = 0,
        title: String? = null,
    ): String {
        val route = "collection/${type.name}/${kind.name}/$genreId"
        return title?.let { "$route?title=${encodeRouteValue(it)}" } ?: route
    }

    fun details(id: MediaId) = "details/${id.type.name}/${id.value}"
}

sealed interface StreamDestination {
    data class Genres(val type: MediaType) : StreamDestination

    data class Collection(
        val type: MediaType,
        val kind: CollectionKind,
        val genreId: Int = 0,
        val title: String? = null,
    ) : StreamDestination

    data class Details(val id: MediaId) : StreamDestination
}

internal fun NavController.navigateToGenres(type: MediaType) {
    navigate(StreamRoutes.genres(type))
}

internal fun NavController.navigateTo(destination: StreamDestination) {
    when (destination) {
        is StreamDestination.Genres -> navigateToGenres(type = destination.type)
        is StreamDestination.Collection ->
            navigateToCollection(
                type = destination.type,
                kind = destination.kind,
                genreId = destination.genreId,
                title = destination.title,
            )
        is StreamDestination.Details -> navigateToDetails(id = destination.id)
    }
}

internal fun NavController.navigateToCollection(
    type: MediaType,
    kind: CollectionKind,
    genreId: Int = 0,
    title: String? = null,
) {
    navigate(StreamRoutes.collection(type, kind, genreId, title))
}

internal fun NavController.navigateToDetails(id: MediaId) {
    navigate(StreamRoutes.details(id))
}
