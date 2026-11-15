plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "domainImplementationDependencyDirection-invalid"

include(
    ":features:example:domain:impl",
    ":features:example:data"
)
