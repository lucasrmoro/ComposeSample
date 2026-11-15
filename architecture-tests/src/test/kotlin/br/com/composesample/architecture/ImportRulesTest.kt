package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.files
import org.junit.Test

class ImportRulesTest {

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

    private fun String.isForbiddenPresentationImport(): Boolean =
        startsWith("android.app.") ||
            startsWith("android.view.") ||
            startsWith("android.widget.") ||
            startsWith("androidx.compose.material.") ||
            startsWith("androidx.compose.material3.") ||
            startsWith("androidx.compose.material.icons.") ||
            startsWith("com.google.android.material.")

}
