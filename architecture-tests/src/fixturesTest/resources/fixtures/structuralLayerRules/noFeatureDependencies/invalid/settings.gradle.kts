plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "noFeatureDependencies-invalid"

include(
    ":structural:media:di",
    ":features:example:domain:api"
)
