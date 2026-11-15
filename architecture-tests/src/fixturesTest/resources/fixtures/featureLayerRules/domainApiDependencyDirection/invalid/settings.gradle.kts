plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "domainApiDependencyDirection-invalid"

include(
    ":features:example:domain:api",
    ":structural:design-system"
)
