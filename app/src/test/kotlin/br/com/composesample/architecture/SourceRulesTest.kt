package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.files
import org.junit.Test

class SourceRulesTest {

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
    fun `production Kotlin source must not use import aliases or wildcard imports`() {
        Konture.files {
            that().satisfy { _: FileDeclarationContext -> true }
            should().satisfy(description = "explicit imports", predicate = { file: FileDeclarationContext ->
                file.declaration.importAliases.isEmpty() &&
                    file.declaration.imports.none { import -> import.endsWith(".*") }
            })
        }
    }

    @Test
    fun `feature presentation must use design system components instead of platform widgets`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.isFeaturePresentationSource() }
            should().satisfy(description = "presentation UI component boundary", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.none { import -> import.isForbiddenPresentationImport() }
            })
        }
    }

    @Test
    fun `feature UI state event and effect declarations must live in screen model packages`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    file.declaration.classes.any { declaration -> declaration.name.isUiContractType() }
            }
            should().satisfy(description = "MVI UI contract placement", predicate = { file: FileDeclarationContext ->
                val relativePackage = file.relativeFeaturePackage()
                relativePackage.endsWith(".model")
            })
        }
    }

    @Test
    fun `structural APIs must import only approved contract types`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.modulePath.matches(structuralApiModulePathPattern) }
            should().satisfy(description = "structural API contract purity", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.all { import -> import.isAllowedStructuralApiImport() }
            })
        }
    }

    @Test
    fun `feature domain APIs must import only contract types`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.modulePath.matches(featureDomainApiModulePathPattern) }
            should().satisfy(description = "feature domain API purity", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.all { import -> import.isAllowedFeatureDomainApiImport(file.featureName()) }
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
            listOf("domain", "api") -> packageSegments.firstOrNull() in domainApiPackageRoots
            listOf("domain", "impl") -> packageSegments.firstOrNull() == "useCase"
            listOf("presentation") -> packageSegments.isPresentationPackage()
            else -> false
        }
    }

    private fun FileDeclarationContext.relativeFeaturePackage(): String {
        val featureName = featureName()
        return declaration.packageName.removePrefix("$architectureBasePackage.$featureName.")
    }

    private fun List<String>.isPresentationPackage(): Boolean = when (firstOrNull()) {
        "model", "navigation", "tokens" -> true
        "ui" -> true
        null -> false
        else -> size >= 2 && getOrNull(1) in presentationScreenPackageRoots
    }

    private fun String.isForbiddenPresentationImport(): Boolean =
        startsWith("android.app.") ||
            startsWith("android.view.") ||
            startsWith("android.widget.") ||
            startsWith("androidx.compose.material3.") ||
            startsWith("androidx.compose.material.icons.") ||
            startsWith("com.google.android.material.")

    private fun String.isUiContractType(): Boolean =
        endsWith("UiState") || endsWith("UiEvent") || endsWith("UiEffect")

    private fun String.isAllowedStructuralApiImport(): Boolean =
        (startsWith("$architectureBasePackage.structural.") && !importsDataImplementationType()) ||
            isStandardApiImport() || this in structuralApiAllowedPlatformImports

    private fun String.isAllowedFeatureDomainApiImport(featureName: String): Boolean {
        val featurePackage = "$architectureBasePackage.$featureName."
        val isOwnDomainContract = startsWith(featurePackage) &&
            removePrefix(featurePackage).substringBefore('.') in domainApiPackageRoots
        val isStructuralContract = startsWith("$architectureBasePackage.structural.") &&
            !importsDataImplementationType()
        return isOwnDomainContract || isStructuralContract || isStandardApiImport()
    }

    private fun String.isStandardApiImport(): Boolean =
        startsWith("java.") || startsWith("kotlin.") || startsWith("kotlinx.coroutines.flow.")

    private fun String.importsDataImplementationType(): Boolean =
        contains(".dto.") || contains(".entity.")

    private companion object {
        val dataPackageRoots = setOf("dao", "database", "dataSource", "dto", "entity", "mapper", "repository")
        val domainApiPackageRoots = setOf("model", "repository", "useCase")
        val presentationScreenPackageRoots = setOf("model", "screen", "viewModel")
    }

}
