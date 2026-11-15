package br.com.composesample.architecture

import io.github.baole.konture.ClassDeclaration
import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.Module
import io.github.baole.konture.classes
import io.github.baole.konture.files
import io.github.baole.konture.modules
import org.junit.Test

class NavigationRulesTest {

    @Test
    fun `feature presentation modules must declare a complete navigation package`() {
        Konture.modules {
            that().matching { module -> module.path.matches(featurePresentationModulePathPattern) }
            should().satisfy { module: Module, violations: MutableList<String> ->
                val featureName = module.path.featureNameFromModulePath()
                val packageName = expectedNavigationPackage(featureName = featureName)
                val actualFiles = module.files
                    .filter { file -> file.packageName == packageName }
                    .map { it.name }
                    .toSet()
                val missingFiles = expectedNavigationFiles(featureName = featureName) - actualFiles
                if (missingFiles.isNotEmpty()) {
                    violations.add(
                        "${module.path} must declare a complete feature navigation package $packageName; " +
                            "missing files: ${missingFiles.sorted().joinToString()}"
                    )
                }
            }
        }
    }

    @Test
    fun `feature navigation packages must contain only approved feature files`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() && file.declaration.packageName.hasPackageSegment("navigation")
            }
            should().satisfy(
                description = "approved feature navigation files",
                predicate = { file: FileDeclarationContext ->
                    val featureName = file.featureName()
                    file.declaration.packageName == expectedNavigationPackage(featureName = featureName) &&
                        file.declaration.name in expectedNavigationFiles(featureName = featureName)
                }
            )
        }
    }

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
    fun `feature navigation must use shared navigation contracts`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() && file.declaration.packageName.hasPackageSegment("navigation")
            }
            should().satisfy(
                description = "feature navigation shared contracts",
                predicate = { file: FileDeclarationContext ->
                    file.declaration.imports.any { import ->
                        import.startsWith("$architectureBasePackage.structural.navigation.")
                    }
                }
            )
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

    private fun expectedNavigationPackage(featureName: String): String =
        "$architectureBasePackage.$featureName.navigation"

    private fun expectedNavigationFiles(featureName: String): Set<String> {
        val prefix = featureName.replaceFirstChar(Char::uppercase)
        return setOf("${prefix}EntryPointImpl.kt", "${prefix}NavHost.kt", "${prefix}Routes.kt")
    }

}
