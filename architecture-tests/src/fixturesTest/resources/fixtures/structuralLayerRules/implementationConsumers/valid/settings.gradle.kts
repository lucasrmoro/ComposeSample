plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "implementationConsumers-valid"

include(
    ":structural:media:di",
    ":structural:media:impl"
)
