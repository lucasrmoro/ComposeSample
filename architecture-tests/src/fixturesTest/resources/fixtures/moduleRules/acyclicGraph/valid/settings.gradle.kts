plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "acyclicGraph-valid"

include(
    ":features:example:data",
    ":features:example:domain:api"
)
