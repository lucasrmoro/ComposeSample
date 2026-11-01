package br.com.composesample.structural.navigation.core

import androidx.compose.runtime.staticCompositionLocalOf

/** Requests navigation to a feature entry point from the currently hosted feature. */
fun interface Navigator {

    /** Pushes a new feature entry onto the app-level feature back stack. */
    fun navigate(destination: FeatureDestination)

}

/** Provides cross-feature navigation to feature content without exposing the root stack. */
val LocalFeatureNavigator = staticCompositionLocalOf<Navigator> {
    error("Feature navigation is only available below a registered feature entry point")
}
