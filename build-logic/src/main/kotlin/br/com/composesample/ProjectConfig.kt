package br.com.composesample

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object ProjectConfig {
    const val APP_ID = "br.com.composesample"
    const val NAMESPACE = "br.com.composesample"
    const val MIN_SDK = 26
    const val COMPILE_SDK = 36
    const val ANDROID_JUNIT_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
    val JVM_TARGET = JvmTarget.JVM_17
    val JAVA_VERSION = JavaVersion.VERSION_17
    const val PROGUARD_ANDROID_OPTIMIZE = "proguard-android-optimize.txt"
    const val PROGUARD_RULES = "proguard-rules.pro"
    val COMPOSE_MODULES = setOf(
        ":app",
        ":features:stream:presentation",
        ":structural:design-system",
        ":structural:di:api",
        ":structural:navigation:api"
    )
}

internal object Plugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
    const val COMPOSE_COMPILER = "org.jetbrains.kotlin.plugin.compose"
    const val KSP = "com.google.devtools.ksp"
}

internal object Flavors {
    const val DEV = "dev"
    const val PRD = "prd"
    const val DEBUG_APP_ID_SUFFIX = ".debug"
    const val DEBUG_APP_VERSION_NAME_SUFFIX = "-debug"
    const val DEFAULT_DIMEN = "default"
}

internal object BuildTypes {
    const val DEBUG = "debug"
    const val RELEASE = "release"
}

internal object ConfigField {
    const val API_BASE_URL = "API_BASE_URL"
    const val API_BASE_URL_PRD = "https://api-base-url-prd.com"
    const val API_BASE_URL_DEV = "https://api-base-url-dev.com"
}
