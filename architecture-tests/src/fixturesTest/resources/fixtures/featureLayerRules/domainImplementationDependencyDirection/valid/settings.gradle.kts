plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "domainImplementationDependencyDirection-valid"

include(
    ":features:example:domain:impl",
    ":features:example:domain:api"
)
