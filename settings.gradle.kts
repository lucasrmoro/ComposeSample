import java.util.Properties

val localProperties = Properties().apply {
    settingsDir.resolve("local.properties").takeIf { it.isFile }?.inputStream()?.use(::load)
}
val tmdbReadAccessToken =
    providers.environmentVariable("TMDB_READ_ACCESS_TOKEN").orNull?.takeIf(String::isNotBlank)
        ?: localProperties.getProperty("TMDB_READ_ACCESS_TOKEN").orEmpty()
gradle.extra["tmdbReadAccessToken"] = tmdbReadAccessToken

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Compose - Sample"

include(":app")
// Structural
include(
    ":structural",
    ":structural:analytics:api",
    ":structural:analytics:di",
    ":structural:analytics:impl",
    ":structural:assets-cache:api",
    ":structural:assets-cache:di",
    ":structural:assets-cache:impl",
    ":structural:biometric:api",
    ":structural:biometric:di",
    ":structural:biometric:impl",
    ":structural:camera:api",
    ":structural:camera:di",
    ":structural:camera:impl",
    ":structural:core",
    ":structural:core:di",
    ":structural:design-system",
    ":structural:design-system:di",
    ":structural:di:api",
    ":structural:di:impl",
    ":structural:local-storage",
    ":structural:local-storage:api",
    ":structural:local-storage:di",
    ":structural:local-storage:impl",
    ":structural:localization:api",
    ":structural:localization:di",
    ":structural:localization:impl",
    ":structural:logger:api",
    ":structural:logger:di",
    ":structural:logger:impl",
    ":structural:media-compression:api",
    ":structural:media-compression:di",
    ":structural:media-compression:impl",
    ":structural:media:api",
    ":structural:media:di",
    ":structural:media:impl",
    ":structural:navigation:api",
    ":structural:navigation:di",
    ":structural:navigation:impl",
    ":structural:network",
    ":structural:network:api",
    ":structural:network:di",
    ":structural:network:impl",
    ":structural:notifications:api",
    ":structural:notifications:di",
    ":structural:notifications:impl",
    ":structural:permissions:api",
    ":structural:permissions:di",
    ":structural:permissions:impl",
    ":structural:push-notifications:api",
    ":structural:push-notifications:di",
    ":structural:push-notifications:impl",
    ":structural:remote-config:api",
    ":structural:remote-config:di",
    ":structural:remote-config:impl",
    ":structural:security:api",
    ":structural:security:di",
    ":structural:security:impl",
    ":structural:session:api",
    ":structural:session:di",
    ":structural:session:impl",
    ":structural:video-player:api",
    ":structural:video-player:di",
    ":structural:video-player:impl"
)

// Features
include(
    ":features",
    ":features:login:analytics",
    ":features:login:data",
    ":features:login:di",
    ":features:login:domain:api",
    ":features:stream",
    ":features:stream:analytics",
    ":features:stream:data",
    ":features:stream:di",
    ":features:stream:domain:api",
    ":features:stream:domain:impl",
    ":features:stream:presentation"
)
