plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "implementationConsumers-invalid"

include(
    ":structural:camera:di",
    ":structural:media:impl"
)
