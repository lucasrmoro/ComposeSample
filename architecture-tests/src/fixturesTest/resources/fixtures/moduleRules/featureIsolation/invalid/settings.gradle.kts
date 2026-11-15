plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "featureIsolation-invalid"

include(
    ":features:example:data",
    ":features:otherexample:domain:api"
)
