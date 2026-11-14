package br.com.composesample.architecture

import io.github.baole.konture.Konture
import io.github.baole.konture.modules
import org.junit.Test

class ModuleRulesTest {

    @Test
    fun `module graph must be acyclic`() {
        Konture.projectGraph.assertNoCycles()
    }

    @Test
    fun `project must use only approved top level and leaf modules`() {
        Konture.modules {
            that().matching { true }
            should().satisfy { module, violations ->
                if (!module.path.isApprovedModulePath()) {
                    violations.add("${module.path} is outside the approved module layout")
                }
            }
        }
    }

    @Test
    fun `feature modules must not depend on sibling features`() {
        Konture.modules {
            that().haveNameMatching(":features:**")
            should().satisfy { module, violations ->
                val featureName = module.path.featureNameFromModulePath()
                module.projectDependencyPaths()
                    .filter { dependency -> dependency.startsWith(":features:") }
                    .filter { dependency ->
                        dependency.featureNameFromModulePath() != featureName
                    }
                    .forEach { dependency ->
                        violations.add("${module.path} depends on sibling feature module $dependency")
                    }
            }
        }
    }

    @Test
    fun `api reexports must use the approved allowlist`() {
        Konture.modules {
            that().matching { true }
            should().satisfy { module, violations ->
                module.dependencies
                    .filter { dependency -> dependency.configuration == "api" }
                    .map { dependency -> module.path to dependency.targetPath }
                    .filterNot { edge -> edge in allowedApiReexports }
                    .forEach { edge ->
                        violations.add("${edge.first} reexports ${edge.second} without an architecture allowlist entry")
                    }
            }
        }
    }

    private fun String.isApprovedModulePath(): Boolean {
        if (this == ":" || this == ":app" || this == ":features" || this == ":structural") return true

        val segments = removePrefix(":").split(':')
        return when (segments.firstOrNull()) {
            "features" -> isApprovedFeatureModule(segments)
            "structural" -> isApprovedStructuralModule(segments)
            else -> false
        }
    }

    private fun isApprovedFeatureModule(segments: List<String>): Boolean = when (segments.size) {
        2 -> true
        3 -> segments[2] in featureModuleNames || segments[2] == "domain"
        4 -> segments[2] == "domain" && segments[3] in setOf("api", "impl")
        else -> false
    }

    private fun isApprovedStructuralModule(segments: List<String>): Boolean = when {
        segments.size == 2 -> segments[1].isNotBlank()
        segments.size == 3 && segments[1] in setOf("core", "design-system") -> segments[2] == "di"
        segments.size == 3 && segments[1] == "di" -> segments[2] in setOf("api", "impl")
        segments.size == 3 -> segments[2] in setOf("api", "di", "impl")
        else -> false
    }

    private companion object {
        val featureModuleNames = setOf("analytics", "data", "di", "presentation")
        val allowedApiReexports = setOf(
            ":structural:core" to ":structural:session:api",
            ":structural:core" to ":structural:network:api",
            ":structural:core" to ":structural:di:api",
            ":features:login:analytics" to ":structural:analytics:api",
            ":structural:assets-cache:api" to ":structural:media:api",
            ":structural:camera:api" to ":structural:media:api",
            ":structural:local-storage:api" to ":structural:biometric:api",
            ":structural:media-compression:api" to ":structural:media:api",
            ":structural:network:api" to ":structural:di:api",
            ":structural:permissions:api" to ":structural:di:api",
            ":structural:push-notifications:api" to ":structural:logger:api"
        )
    }

}
