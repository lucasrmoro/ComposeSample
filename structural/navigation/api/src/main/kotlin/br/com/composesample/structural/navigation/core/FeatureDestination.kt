package br.com.composesample.structural.navigation.core

/** Identifies a feature entry route. Route arguments belong in implementations of this contract. */
interface FeatureDestination {

    /** Stable route used to resolve the registered feature entry point. */
    val route: String

}
