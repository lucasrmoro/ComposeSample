package br.com.composesample.architecture

import io.github.baole.konture.ClassDeclaration
import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.classes
import io.github.baole.konture.files
import org.junit.Test

class NavigationRulesTest {

    @Test
    fun `navigation controller references must stay inside feature navigation packages`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    (file.declaration.imports.any { import -> import.isNavigationControllerType() } ||
                        file.declaration.usages.any { usage -> usage.targetFqName.isNavigationControllerType() })
            }
            should().satisfy(description = "navigation encapsulation", predicate = { file: FileDeclarationContext ->
                file.declaration.packageName.substringAfterLast('.') == "navigation"
            })
        }
    }

    @Test
    fun `feature route declarations must use the feature prefix`() {
        Konture.classes {
            that().satisfy { declaration: ClassDeclaration ->
                declaration.packageName.hasPackageSegment("navigation") &&
                    !declaration.packageName.contains(".structural.") &&
                    (declaration.name.endsWith("Route") || declaration.name.endsWith("Routes") ||
                        declaration.name.endsWith("Destination"))
            }
            should().satisfy(description = "feature route naming", predicate = { declaration: ClassDeclaration ->
                val featureName = declaration.packageName
                    .removePrefix("$architectureBasePackage.")
                    .substringBefore('.')
                declaration.name.startsWith(featureName.replaceFirstChar { character -> character.uppercase() })
            })
        }
    }

}
