plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "presentationDependencyDirection-valid"

include(
    ":features:example:presentation",
    ":features:example:domain:api"
)
