plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "noDiDependencies-invalid"

include(
    ":structural:media:impl",
    ":structural:design-system:di"
)
