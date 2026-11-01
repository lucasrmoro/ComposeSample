package br.com.composesample.structural.navigation.core

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

private const val NAVIGATION_TRANSITION_DURATION_MILLIS = 500

/**
 * Hosts a navigation graph with shared horizontal transitions for forward and back navigation.
 *
 * Forward navigation enters from the right and exits to the left. Back navigation uses the
 * opposite directions so the previous destination returns from the left.
 */
@Composable
fun AnimatedNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = route,
        enterTransition = { forwardEnterTransition() },
        exitTransition = { forwardExitTransition() },
        popEnterTransition = { backEnterTransition() },
        popExitTransition = { backExitTransition() },
        builder = builder
    )
}

private fun forwardEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS)
    )

private fun forwardExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS)
    )

private fun backEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS)
    )

private fun backExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS)
    )
