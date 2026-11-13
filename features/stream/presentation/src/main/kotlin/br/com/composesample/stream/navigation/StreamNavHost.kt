package br.com.composesample.stream.navigation

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
import br.com.composesample.stream.model.catalog.CollectionKind
import br.com.composesample.stream.model.media.MediaId
import br.com.composesample.stream.model.media.MediaType
import br.com.composesample.structural.di.dependencyViewModel
import br.com.composesample.structural.navigation.core.AnimatedNavHost

@Composable
internal fun StreamNavHost() {
    val controller = rememberNavController()
    val back: () -> Unit = { controller.popBackStack() }

    AnimatedNavHost(controller, startDestination = StreamRoutes.CATALOG) {
        composable(StreamRoutes.CATALOG) {
            val viewModel: CatalogViewModel = dependencyViewModel()
            CatalogScreen(viewModel) { effect ->
                when (effect) {
                    is CatalogUiEffect.Details -> controller.navigateToDetails(effect.id)
                    is CatalogUiEffect.Genres -> controller.navigateToGenres(effect.type)
                    is CatalogUiEffect.Collection -> controller.navigateToCollection(
                        type = effect.type,
                        kind = effect.kind
                    )
                }
            }.Init()
        }
        composable(StreamRoutes.GENRES) { entry ->
            val viewModel: GenresViewModel = dependencyViewModel()
            val type = MediaType.valueOf(
                requireNotNull(entry.arguments?.getString(StreamRoutes.Arguments.TYPE))
            )
            GenresScreen(viewModel, type, back) { effect ->
                when (effect) {
                    is GenresUiEffect.Collection -> controller.navigateToCollection(
                        type = effect.type,
                        kind = CollectionKind.GENRE,
                        genreId = effect.genre.id,
                        title = effect.genre.name
                    )
                }
            }.Init()
        }
        composable(
            StreamRoutes.COLLECTION,
            arguments = listOf(
                navArgument(StreamRoutes.Arguments.GENRE) { type = NavType.IntType },
                navArgument(StreamRoutes.Arguments.TITLE) {
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
                MediaType.valueOf(requireNotNull(args.getString(StreamRoutes.Arguments.TYPE))),
                CollectionKind.valueOf(
                    requireNotNull(args.getString(StreamRoutes.Arguments.KIND))
                ),
                args.getInt(StreamRoutes.Arguments.GENRE).takeIf { it > 0 },
                args.getString(StreamRoutes.Arguments.TITLE),
                back
            ) { effect ->
                when (effect) {
                    is CollectionUiEffect.Details -> controller.navigateToDetails(effect.id)
                }
            }.Init()
        }
        composable(
            StreamRoutes.DETAILS,
            arguments = listOf(
                navArgument(StreamRoutes.Arguments.ID) { type = NavType.LongType }
            )
        ) { entry ->
            val viewModel: DetailsViewModel = dependencyViewModel()
            val args = requireNotNull(entry.arguments)
            DetailsScreen(
                viewModel,
                MediaId(
                    type = MediaType.valueOf(
                        requireNotNull(args.getString(StreamRoutes.Arguments.TYPE))
                    ),
                    value = args.getLong(StreamRoutes.Arguments.ID)
                ),
                back
            ).Init()
        }
    }
}
