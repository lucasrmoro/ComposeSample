package br.com.composesample.structural.navigation.core

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgumentBuilder

/** Creates a Navigation Compose argument using this string as its argument name. */
fun String.navArgument(builder: NavArgumentBuilder.() -> Unit): NamedNavArgument =
    androidx.navigation.navArgument(name = this, builder = builder)
