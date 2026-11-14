package br.com.composesample.architecture

import io.github.baole.konture.ClassDeclaration
import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.Module
import io.github.baole.konture.Visibility
import io.github.baole.konture.classes
import io.github.baole.konture.files
import io.github.baole.konture.modules
import org.junit.Test
import java.io.File

class ComposeRulesTest {

    @Test
    fun `viewmodels must expose uiState through asStateFlow`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    file.declaration.packageName.hasPackageSegment("viewModel") &&
                    file.declaration.classes.any { declaration -> declaration.name.endsWith("ViewModel") }
            }
            should().satisfy(description = "read-only uiState exposure", predicate = { file: FileDeclarationContext ->
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
                    import.isNavigationControllerType() ||
                        import.isSiblingFeatureImport(file.modulePath)
                }
            })
        }
    }

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
    fun `navigation controller references must stay inside feature navigation packages`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.isFeaturePresentationSource() &&
                    (file.declaration.imports.any { import ->
                        import.isNavigationControllerType()
                    } || file.declaration.usages.any { usage ->
                        usage.targetFqName.isNavigationControllerType()
                    })
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

    @Test
    fun `the project must declare exactly one activity in the app module`() {
        Konture.modules {
            that().matching { true }
            should().satisfy { module: Module, violations: MutableList<String> ->
                val activities = module.classes.filter { declaration ->
                    declaration.supertypes.any { supertype -> supertype.substringAfterLast('.').endsWith("Activity") }
                }
                if (module.path == ":app" && activities.size != 1) {
                    violations.add("The app must declare exactly one Activity; found ${activities.size}")
                }
                if (module.path != ":app" && activities.isNotEmpty()) {
                    violations.add("Activity declarations are only allowed in :app, found in ${module.path}")
                }
                if (module.path == ":app") {
                    val projectRoot = findProjectRoot()
                    val manifestActivityCount = listOf("app", "features", "structural")
                        .sumOf { sourceRoot ->
                            projectRoot.resolve(sourceRoot)
                                .walkTopDown()
                                .onEnter { directory -> directory.name != "build" }
                                .filter { file -> file.isFile && file.name == "AndroidManifest.xml" }
                                .sumOf { manifest -> manifest.readText().countActivityDeclarations() }
                        }
                    if (manifestActivityCount != 1) {
                        violations.add("The project must declare exactly one Activity in manifests; found $manifestActivityCount")
                    }
                }
            }
        }
    }

    @Test
    fun `launcher activity must only host the app content`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.modulePath == ":app" &&
                    file.declaration.classes.any { declaration ->
                        declaration.name.endsWith("Activity") ||
                            declaration.supertypes.any { supertype -> supertype.substringAfterLast('.').endsWith("Activity") }
                    }
            }
            should().satisfy(description = "launcher Activity host boundary", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.none { import ->
                    import.startsWith("androidx.navigation.") ||
                        import.contains(".presentation.") ||
                        import.contains(".screen.") ||
                        import.contains(".viewModel.")
                }
            })
        }
    }

    private fun String.isSiblingFeatureImport(modulePath: String): Boolean {
        val ownFeature = modulePath.featureNameFromModulePath()
        val samplePackage = "$architectureBasePackage."
        if (!startsWith(samplePackage)) return false
        val importedPackage = removePrefix(samplePackage)
        val importedFeature = if (importedPackage.startsWith("features.")) {
            importedPackage.removePrefix("features.").substringBefore('.')
        } else {
            importedPackage.substringBefore('.')
        }
        return importedFeature != ownFeature && importedFeature != "structural"
    }

    private fun String.isNavigationControllerType(): Boolean =
        this == "androidx.navigation.NavController" || this == "androidx.navigation.NavHostController"

    private fun findProjectRoot(): File {
        var directory = File(System.getProperty("user.dir") ?: ".").absoluteFile
        while (!directory.resolve("settings.gradle.kts").exists()) {
            val parent = directory.parentFile ?: break
            directory = parent
        }
        return directory
    }

    private fun String.countActivityDeclarations(): Int =
        replace(manifestCommentPattern, "")
            .let { manifest -> manifestActivityPattern.findAll(manifest).count() }

    private companion object {
        val readOnlyUiStatePattern = Regex(
            "(?:override\\s+)?val\\s+uiState(?:\\s*:\\s*[^=]+)?\\s*=\\s*[A-Za-z_]\\w*\\.asStateFlow\\(\\)"
        )
        val manifestCommentPattern = Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL)
        val manifestActivityPattern = Regex("<activity(?=[\\s>])")
    }

}
