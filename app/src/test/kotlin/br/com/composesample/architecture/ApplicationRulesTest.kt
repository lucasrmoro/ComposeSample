package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.Module
import io.github.baole.konture.files
import io.github.baole.konture.modules
import org.junit.Test
import java.io.File

class ApplicationRulesTest {

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
        val manifestCommentPattern = Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL)
        val manifestActivityPattern = Regex("<activity(?=[\\s>])")
    }

}
