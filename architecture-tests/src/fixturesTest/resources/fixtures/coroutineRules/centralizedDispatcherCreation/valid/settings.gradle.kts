plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "centralizedDispatcherCreation-valid"

include(
    ":structural:media:impl",
    ":structural:core:di"
)
