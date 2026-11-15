package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.files
import org.junit.Test

class PackageRulesTest {

    @Test
    fun `feature source files must stay within approved package roots`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.modulePath.startsWith(":features:") }
            should().satisfy(description = "feature package roots", predicate = { file: FileDeclarationContext ->
                file.hasApprovedFeaturePackage()
            })
        }
    }

    @Test
    fun `feature UI state event and effect declarations must live in presentation model packages`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    file.declaration.classes.any { declaration -> declaration.name.isUiContractType() }
            }
            should().satisfy(description = "MVI UI contract placement", predicate = { file: FileDeclarationContext ->
                file.relativeFeaturePackage().substringBefore('.') == "model"
            })
        }
    }

    private fun FileDeclarationContext.hasApprovedFeaturePackage(): Boolean {
        val segments = modulePath.removePrefix(":features:").split(':')
        if (segments.size < 2) return false

        val featureName = segments.first()
        val packagePrefix = "$architectureBasePackage.$featureName"
        val relativePackage = when {
            declaration.packageName == packagePrefix -> ""
            declaration.packageName.startsWith("$packagePrefix.") -> declaration.packageName.removePrefix("$packagePrefix.")
            else -> return false
        }
        val packageSegments = relativePackage.split('.').filter(String::isNotEmpty)

        return when (segments.drop(1)) {
            listOf("analytics") -> packageSegments.firstOrNull() == "analytics"
            listOf("data") -> packageSegments.firstOrNull() in dataPackageRoots
            listOf("di") -> relativePackage.isEmpty() || relativePackage == "di"
            listOf("domain", "api") -> packageSegments.firstOrNull() in featureDomainApiPackageRoots
            listOf("domain", "impl") -> packageSegments.firstOrNull() == "useCase"
            listOf("presentation") -> packageSegments.firstOrNull() in presentationPackageRoots
            else -> false
        }
    }

    private fun FileDeclarationContext.relativeFeaturePackage(): String =
        declaration.packageName.removePrefix("$architectureBasePackage.${featureName()}.")

    private fun String.isUiContractType(): Boolean =
        endsWith("UiState") || endsWith("UiEvent") || endsWith("UiEffect")

    private companion object {
        val dataPackageRoots = setOf("dao", "database", "dataSource", "dto", "entity", "mapper", "repository")
        val presentationPackageRoots = setOf("model", "ui", "viewModel", "navigation", "tokens")
    }

}
