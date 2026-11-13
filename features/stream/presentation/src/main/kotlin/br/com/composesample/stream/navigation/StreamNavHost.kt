package br.com.composesample.stream.navigation

import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.composesample.stream.catalog.model.CatalogUiEffect
import br.com.composesample.stream.catalog.screen.CatalogScreen
import br.com.composesample.stream.catalog.viewModel.CatalogViewModel
import br.com.composesample.stream.collection.model.CollectionUiEffect
import br.com.composesample.stream.collection.screen.CollectionScreen
import br.com.composesample.stream.collection.viewModel.CollectionViewModel
import br.com.composesample.stream.details.screen.DetailsScreen
import br.com.composesample.stream.details.viewModel.DetailsViewModel
import br.com.composesample.stream.genres.model.GenresUiEffect
import br.com.composesample.stream.genres.screen.GenresScreen
import br.com.composesample.stream.genres.viewModel.GenresViewModel
import br.com.composesample.structural.di.dependencyViewModel
import br.com.composesample.structural.navigation.core.AnimatedNavHost
import br.com.composesample.structural.navigation.core.encodeRouteValue

@Composable
internal fun StreamNavHost() {
    val controller = rememberNavController()
    val back: () -> Unit = { controller.popBackStack() }
    fun details(id: MediaId) {
        controller.navigate("details/${id.type.name}/${id.value}")
    }
    AnimatedNavHost(controller, startDestination = "catalog") {
        composable("catalog") {
            val viewModel: CatalogViewModel = dependencyViewModel()
            CatalogScreen(viewModel) { effect ->
                    when (effect) {
                        is CatalogUiEffect.Details -> details(effect.id)
                        is CatalogUiEffect.Genres ->
                            controller.navigate("genres/${effect.type.name}")
                        is CatalogUiEffect.Collection ->
                            controller.navigate(
                                "collection/${effect.type.name}/${effect.kind.name}/0"
                            )
                    }
                }
                .Init()
        }
        composable("genres/{type}") { entry ->
            val viewModel: GenresViewModel = dependencyViewModel()
            val type = MediaType.valueOf(requireNotNull(entry.arguments?.getString("type")))
            GenresScreen(viewModel, type, back) { effect ->
                    when (effect) {
                        is GenresUiEffect.Collection ->
                            controller.navigate(
                                "collection/${effect.type.name}/${CollectionKind.GENRE.name}/${effect.genre.id}?title=${encodeRouteValue(effect.genre.name)}"
                            )
                    }
                }
                .Init()
        }
        composable(
            "collection/{type}/{kind}/{genre}?title={title}",
            arguments =
                listOf(
                    navArgument("genre") { type = NavType.IntType },
                    navArgument("title") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
        ) { entry ->
            val viewModel: CollectionViewModel = dependencyViewModel()
            val args = requireNotNull(entry.arguments)
            CollectionScreen(
                    viewModel,
                    MediaType.valueOf(requireNotNull(args.getString("type"))),
                    CollectionKind.valueOf(requireNotNull(args.getString("kind"))),
                    args.getInt("genre").takeIf { it > 0 },
                    args.getString("title"),
                    back
                ) { effect ->
                    when (effect) {
                        is CollectionUiEffect.Details -> details(effect.id)
                    }
                }
                .Init()
        }
        composable(
            "details/{type}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val viewModel: DetailsViewModel = dependencyViewModel()
            val args = requireNotNull(entry.arguments)
            DetailsScreen(
                    viewModel,
                    MediaId(
                        type = MediaType.valueOf(requireNotNull(args.getString("type"))),
                        value = args.getLong("id")
                    ),
                    back
                )
                .Init()
        }
    }
}
