package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.files
import org.junit.Test

class ScreenRulesTest {

    @Test
    fun `screen packages must not resolve dependencies or invoke domain services directly`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    file.declaration.packageName.hasPackageSegment("screen")
            }
            should().satisfy(description = "screen dependency boundary", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.none { import ->
                    import.startsWith("$architectureBasePackage.structural.di.") ||
                        import.contains(".data.repository.") ||
                        import.contains(".dataSource.") ||
                        import.contains(".domain.impl.") ||
                        import.endsWith("Repository") ||
                        import.endsWith("UseCase") ||
                        import.isSiblingFeatureImport(file.modulePath)
                }
            })
        }
    }

    @Test
    fun `feature screen classes must inherit from BaseScreen`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    file.declaration.packageName.hasPackageSegment("screen") &&
                    file.declaration.classes.isNotEmpty()
            }
            should().satisfy(description = "feature screen BaseScreen inheritance", predicate = { file: FileDeclarationContext ->
                file.declaration.classes.all { declaration ->
                    declaration.supertypes.any { supertype ->
                        supertype.substringBefore('<').substringAfterLast('.') == "BaseScreen"
                    }
                }
            })
        }
    }

}
