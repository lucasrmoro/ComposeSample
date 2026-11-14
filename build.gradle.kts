import org.gradle.api.GradleException
import org.gradle.api.artifacts.ExternalModuleDependency

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.jetbrains.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
}

tasks.register("architectureCheck") {
    group = "verification"
    description = "Runs Kotlin architecture boundary tests with Konture."
    dependsOn("generateArchitectureLayout")
    dependsOn(":app:testDevDebugUnitTest")
}

val architectureBaseExternalDependencies = setOf(
    "androidx.annotation:annotation-jvm",
    "org.jetbrains.kotlin:kotlin-stdlib",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core"
)
val architectureExternalDependenciesByModule = mapOf(
    ":structural:camera:api" to setOf("androidx.activity:activity"),
    ":structural:di:api" to setOf(
        "androidx.compose:compose-bom",
        "androidx.compose.runtime:runtime",
        "androidx.lifecycle:lifecycle-viewmodel-compose",
        "androidx.lifecycle:lifecycle-viewmodel-savedstate"
    ),
    ":structural:navigation:api" to setOf(
        "androidx.compose:compose-bom",
        "androidx.compose.animation:animation",
        "androidx.compose.runtime:runtime",
        "androidx.navigation:navigation-runtime-ktx",
        "androidx.navigation:navigation-compose"
    )
)

val validateArchitectureExternalDependencies = tasks.register("validateArchitectureExternalDependencies") {
    group = "verification"
    description = "Checks external dependencies against architecture allowlists for restricted modules."

    doLast {
        val restrictedModules = allprojects.filter { project ->
            project.path.matches(Regex("^:features:[^:]+:domain:(api|impl)$")) ||
                project.path.matches(Regex("^:structural:[^:]+:api$"))
        }
        val violations = restrictedModules.flatMap { project ->
            val allowedDependencies = architectureBaseExternalDependencies +
                architectureExternalDependenciesByModule[project.path].orEmpty()
            project.configurations
                .filter { configuration ->
                    val name = configuration.name.lowercase()
                    !name.contains("test") &&
                        listOf("api", "implementation", "compileonly", "runtimeonly")
                            .any { suffix -> name == suffix || name.endsWith(suffix) }
                }
                .flatMap { configuration ->
                    configuration.dependencies
                        .filterIsInstance<ExternalModuleDependency>()
                        .mapNotNull { dependency ->
                            dependency.group?.let { group ->
                                Triple(configuration.name, "$group:${dependency.name}", project.path)
                            }
                        }
                }
                .distinctBy { dependency -> dependency.first to dependency.second }
                .filterNot { dependency -> dependency.second in allowedDependencies }
                .map { dependency ->
                    "${dependency.third} declares ${dependency.second} in ${dependency.first}, " +
                        "which is not in its architecture allowlist"
                }
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                "External dependency architecture violations:\n${violations.joinToString(separator = "\n") { "- $it" }}"
            )
        }
    }
}

tasks.named("architectureCheck") {
    dependsOn(validateArchitectureExternalDependencies)
    dependsOn(
        ":app:processDevDebugMainManifest",
        ":app:processDevReleaseMainManifest",
        ":app:processPrdDebugMainManifest",
        ":app:processPrdReleaseMainManifest"
    )
}

project(":app").tasks.named("testDevDebugUnitTest") {
    dependsOn(
        ":app:processDevDebugMainManifest",
        ":app:processDevReleaseMainManifest",
        ":app:processPrdDebugMainManifest",
        ":app:processPrdReleaseMainManifest"
    )
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}
