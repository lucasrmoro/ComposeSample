package br.com.composesample.structural.navigation.core

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/** Creates a non-null integer navigation argument with an optional default value. */
fun intArgument(
    key: String,
    defaultValue: Int? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.IntType
    defaultValue?.let { this.defaultValue = it }
}

/** Creates a non-null long navigation argument with an optional default value. */
fun longArgument(
    key: String,
    defaultValue: Long? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.LongType
    defaultValue?.let { this.defaultValue = it }
}

/** Creates a non-null float navigation argument with an optional default value. */
fun floatArgument(
    key: String,
    defaultValue: Float? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.FloatType
    defaultValue?.let { this.defaultValue = it }
}

/** Creates a non-null boolean navigation argument with an optional default value. */
fun booleanArgument(
    key: String,
    defaultValue: Boolean? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.BoolType
    defaultValue?.let { this.defaultValue = it }
}

/**
 * Creates a string navigation argument with [key] as its name and optional null/default behavior.
 */
fun stringArgument(
    key: String,
    nullable: Boolean = false,
    defaultValue: String? = null
): NamedNavArgument =
    navArgument(name = key) {
        type = NavType.StringType
        this.nullable = nullable
        defaultValue?.let { this.defaultValue = it }
    }
