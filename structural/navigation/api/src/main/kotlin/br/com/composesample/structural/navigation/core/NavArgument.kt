package br.com.composesample.structural.navigation.core

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/** Creates an integer navigation argument with optional nullability and a default value. */
fun intArgument(
    key: String,
    nullable: Boolean = false,
    defaultValue: Int? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.IntType
    this.nullable = nullable
    defaultValue?.let { this.defaultValue = it }
}

/** Creates a long navigation argument with optional nullability and a default value. */
fun longArgument(
    key: String,
    nullable: Boolean = false,
    defaultValue: Long? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.LongType
    this.nullable = nullable
    defaultValue?.let { this.defaultValue = it }
}

/** Creates a float navigation argument with optional nullability and a default value. */
fun floatArgument(
    key: String,
    nullable: Boolean = false,
    defaultValue: Float? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.FloatType
    this.nullable = nullable
    defaultValue?.let { this.defaultValue = it }
}

/** Creates a boolean navigation argument with optional nullability and a default value. */
fun booleanArgument(
    key: String,
    nullable: Boolean = false,
    defaultValue: Boolean? = null
): NamedNavArgument = navArgument(name = key) {
    type = NavType.BoolType
    this.nullable = nullable
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
