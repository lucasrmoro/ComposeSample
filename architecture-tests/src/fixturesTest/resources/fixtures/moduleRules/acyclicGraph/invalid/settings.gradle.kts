plugins {
    id("io.github.baole.konture") version "1.0.4"
}

rootProject.name = "acyclicGraph-invalid"

include(
    ":features:example:data",
    ":features:example:domain:api"
)
