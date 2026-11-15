plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "approvedApiReexports-valid"

include(
    ":structural:camera:api",
    ":structural:media:api"
)
