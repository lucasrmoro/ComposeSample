package br.com.composesample.stream.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import br.com.composesample.structural.di.parametersOf
import br.com.composesample.structural.navigation.core.AnimatedNavHost
import br.com.composesample.structural.navigation.core.intArgument
import br.com.composesample.structural.navigation.core.longArgument
import br.com.composesample.structural.navigation.core.stringArgument

@Composable
internal fun StreamNavHost() {
    val controller = rememberNavController()
    val back: () -> Unit = { controller.popBackStack() }

    AnimatedNavHost(controller, startDestination = StreamRoutes.CATALOG) {
        composable(StreamRoutes.CATALOG) {
            val viewModel: CatalogViewModel = dependencyViewModel()
            CatalogScreen(
                catalogViewModel = viewModel,
                onNavigate = { destination -> controller.navigateTo(destination) }
            ).Init()
        }
        composable(StreamRoutes.GENRES) { entry ->
            val viewModel: GenresViewModel = dependencyViewModel()
            val type = MediaType.valueOf(
                requireNotNull(entry.arguments?.getString(StreamRoutes.Arguments.TYPE))
            )
            GenresScreen(
                genresViewModel = viewModel,
                type = type,
                back = back,
                onNavigate = { effect ->
                    when (effect) {
                        is GenresUiEffect.Collection -> controller.navigateToCollection(
                            type = effect.type,
                            kind = CollectionKind.GENRE,
                            genreId = effect.genre.id,
                            title = effect.genre.name
                        )
                    }
                }
            ).Init()
        }
        composable(
            StreamRoutes.COLLECTION,
            arguments = listOf(
                intArgument(key = StreamRoutes.Arguments.GENRE),
                stringArgument(key = StreamRoutes.Arguments.TITLE, nullable = true)
            )
        ) { entry ->
            val args = requireNotNull(entry.arguments)
            val type = MediaType.valueOf(
                requireNotNull(args.getString(StreamRoutes.Arguments.TYPE))
            )
            val kind = CollectionKind.valueOf(
                requireNotNull(args.getString(StreamRoutes.Arguments.KIND))
            )
            val genreId = args.getInt(StreamRoutes.Arguments.GENRE).takeIf { it > 0 }
            val viewModel: CollectionViewModel = dependencyViewModel(
                parameters = parametersOf(type, kind, genreId ?: 0)
            )
            CollectionScreen(
                collectionViewModel = viewModel,
                kind = kind,
                title = args.getString(StreamRoutes.Arguments.TITLE),
                back = back,
                onNavigate = { effect ->
                    when (effect) {
                        is CollectionUiEffect.Details -> controller.navigateToDetails(effect.id)
                    }
                }
            ).Init()
        }
        composable(
            route = StreamRoutes.DETAILS,
            arguments = listOf(
                longArgument(key = StreamRoutes.Arguments.ID)
            )
        ) { entry ->
            val args = requireNotNull(entry.arguments)
            val mediaId =
                MediaId(
                    type =
                        MediaType.valueOf(
                            requireNotNull(args.getString(StreamRoutes.Arguments.TYPE))
                        ),
                    value = args.getLong(StreamRoutes.Arguments.ID)
                )
            val viewModel: DetailsViewModel =
                dependencyViewModel(parameters = parametersOf(mediaId))
            DetailsScreen(
                detailsViewModel = viewModel,
                back = back
            ).Init()
        }
    }
}
