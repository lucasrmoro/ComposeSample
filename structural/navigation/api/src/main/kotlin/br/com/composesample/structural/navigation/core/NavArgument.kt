package br.com.composesample.structural.navigation.core

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/** Creates a required integer navigation argument with [key] as its name. */
fun intArgument(key: String): NamedNavArgument = navArgument(name = key) { type = NavType.IntType }

/** Creates a required long navigation argument with [key] as its name. */
fun longArgument(key: String): NamedNavArgument =
    navArgument(name = key) { type = NavType.LongType }

/** Creates a required float navigation argument with [key] as its name. */
fun floatArgument(key: String): NamedNavArgument =
    navArgument(name = key) { type = NavType.FloatType }

/** Creates a required boolean navigation argument with [key] as its name. */
fun booleanArgument(key: String): NamedNavArgument =
    navArgument(name = key) { type = NavType.BoolType }

/**
 * Creates a string navigation argument with [key] as its name and optional null/default behavior.
 */
fun stringArgument(
    key: String,
    nullable: Boolean = false,
    defaultValue: String? = null,
): NamedNavArgument =
    navArgument(name = key) {
        type = NavType.StringType
        this.nullable = nullable
        this.defaultValue = defaultValue
    }
