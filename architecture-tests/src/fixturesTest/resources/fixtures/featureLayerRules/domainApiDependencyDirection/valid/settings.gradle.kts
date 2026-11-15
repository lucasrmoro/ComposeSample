plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "domainApiDependencyDirection-valid"

include(
    ":features:example:domain:api",
    ":structural:media:api"
)
