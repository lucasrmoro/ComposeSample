package br.com.composesample.architecture

import io.github.baole.konture.ClassDeclaration
import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.Visibility
import io.github.baole.konture.classes
import io.github.baole.konture.files
import org.junit.Test
import java.io.File

class ViewModelRulesTest {

    @Test
    fun `viewmodels must expose private _uiState as public uiState through asStateFlow`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    file.declaration.packageName.hasPackageSegment("viewModel") &&
                    file.declaration.classes.any { declaration -> declaration.name.endsWith("ViewModel") }
            }
            should().satisfy(description = "private and read-only ViewModel state naming", predicate = { file: FileDeclarationContext ->
                readOnlyUiStatePattern.containsMatchIn(File(file.declaration.filePath).readText())
            })
        }
    }

    @Test
    fun `viewmodels must not expose mutable state holders`() {
        Konture.classes {
            that().satisfy { declaration: ClassDeclaration ->
                declaration.packageName.hasPackageSegment("viewModel") && declaration.name.endsWith("ViewModel")
            }
            should().satisfy(description = "read-only ViewModel state", predicate = { declaration: ClassDeclaration ->
                declaration.properties.none { property ->
                    property.visibility == Visibility.PUBLIC &&
                        (property.type.contains("MutableStateFlow") || property.type.contains("MutableState<"))
                }
            })
        }
    }

    @Test
    fun `viewmodels must not control navigation or depend on sibling feature UI`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    file.declaration.packageName.hasPackageSegment("viewModel")
            }
            should().satisfy(description = "ViewModel navigation and feature boundary", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.none { import ->
                    import.isNavigationControllerType() || import.isSiblingFeatureImport(file.modulePath)
                }
            })
        }
    }

    private companion object {
        val readOnlyUiStatePattern = Regex(
            "private\\s+val\\s+_uiState(?:\\s*:\\s*[^=]+)?\\s*=\\s*MutableStateFlow(?:<[^=]*>)?\\s*\\(" +
                "(?s).*?override\\s+val\\s+uiState(?:\\s*:\\s*[^=]+)?\\s*=\\s*_uiState\\.asStateFlow\\(\\)"
        )
    }

}
