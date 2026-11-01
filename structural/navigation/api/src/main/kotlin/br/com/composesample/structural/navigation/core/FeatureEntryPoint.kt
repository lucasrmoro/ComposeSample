package br.com.composesample.structural.navigation.core

import androidx.compose.runtime.Composable

/**
 * Defines the Compose entry point for a feature and its internal navigation host.
 * Each registered feature owns its own NavHost and internal back stack.
 */
interface FeatureEntryPoint {

    /** Stable route used by the app-level feature stack. */
    val route: String

    /** Renders the feature root and receives the app-level navigator for cross-feature requests. */
    @Composable
    fun Content(navigator: Navigator)

}
