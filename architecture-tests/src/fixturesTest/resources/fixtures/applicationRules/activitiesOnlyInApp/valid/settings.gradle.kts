plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "activitiesOnlyInApp-valid"

include(
    ":app",
    ":features:example:presentation"
)
