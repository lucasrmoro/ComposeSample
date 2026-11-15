plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "noDiDependencies-valid"

include(
    ":structural:media:impl",
    ":structural:media:api"
)
