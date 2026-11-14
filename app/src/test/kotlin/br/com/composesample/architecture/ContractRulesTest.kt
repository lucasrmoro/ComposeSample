package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.files
import org.junit.Test

class ContractRulesTest {

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

    private fun String.isAllowedStructuralApiImport(): Boolean =
        isStructuralApiImport() ||
            isStandardApiImport() || this in structuralApiAllowedPlatformImports

    private fun String.isAllowedFeatureDomainApiImport(featureName: String): Boolean {
        val featurePackage = "$architectureBasePackage.$featureName."
        val isOwnDomainContract = startsWith(featurePackage) &&
            removePrefix(featurePackage).substringBefore('.') in featureDomainApiPackageRoots
        val isStructuralContract = isStructuralApiImport()
        return isOwnDomainContract || isStructuralContract || isStandardApiImport()
    }

    private fun String.isStandardApiImport(): Boolean =
        startsWith("java.") || startsWith("kotlin.") || startsWith("kotlinx.coroutines.flow.")
}
