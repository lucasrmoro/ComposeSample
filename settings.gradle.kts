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
    ":structural:navigation:api",
    ":structural:navigation:impl",
    ":structural:navigation:di",
    ":structural:di:api",
    ":structural:di:impl",
    ":structural:analytics:api",
    ":structural:analytics:impl",
    ":structural:analytics:di",
    ":structural:remote-config:api",
    ":structural:remote-config:impl",
    ":structural:remote-config:di",
    ":structural:notifications:api",
    ":structural:notifications:impl",
    ":structural:notifications:di",
    ":structural:push-notifications:api",
    ":structural:push-notifications:impl",
    ":structural:push-notifications:di",

    ":structural:core",
    ":structural:core:di",
    ":structural:biometric:api",
    ":structural:biometric:impl",
    ":structural:biometric:di",
    ":structural:camera:api",
    ":structural:camera:impl",
    ":structural:camera:di",
    ":structural:media:api",
    ":structural:media:impl",
    ":structural:media:di",
    ":structural:media-compression:api",
    ":structural:media-compression:impl",
    ":structural:media-compression:di",
    ":structural:session:api",
    ":structural:session:impl",
    ":structural:session:di",
    ":structural:permissions:api",
    ":structural:permissions:impl",
    ":structural:permissions:di",
    ":structural:logger:api",
    ":structural:logger:impl",
    ":structural:logger:di",
    ":structural:network",
    ":structural:network:api",
    ":structural:network:impl",
    ":structural:network:di",
    ":structural:design-system",
    ":structural:design-system:di",
    ":structural:local-storage",
    ":structural:local-storage:api",
    ":structural:local-storage:impl",
    ":structural:local-storage:di"
)

// Features
include(
    ":features",
    ":features:login:analytics",
    ":features:featureA:analytics",
    ":features:featureA",
    ":features:featureA:di",
    ":features:featureA:data",
    ":features:featureA:domain:api",
    ":features:featureA:domain:impl",
    ":features:featureA:presentation",
    ":features:login:domain:api",
    ":features:login:data",
    ":features:login:di"
)
