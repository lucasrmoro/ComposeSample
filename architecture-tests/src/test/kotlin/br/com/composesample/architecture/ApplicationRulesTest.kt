package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.Module
import io.github.baole.konture.files
import io.github.baole.konture.modules
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ApplicationRulesTest {

    @Test
    fun `the project must declare exactly one activity in the app module`() {
        val activityTypes = activityTypes()
        Konture.modules {
            that().matching { module -> module.path == ":app" }
            should().satisfy { module: Module, violations: MutableList<String> ->
                val activities = module.classes.filter(activityTypes::isActivity)
                if (activities.size != 1) {
                    violations.add("The app must declare exactly one Activity; found ${activities.size}")
                }
                violations.addAll(mergedManifestViolations())
            }
        }
    }

    @Test
    fun `activities must be declared only in the app module`() {
        val activityTypes = activityTypes()
        Konture.modules {
            that().matching { true }
            should().satisfy { module: Module, violations: MutableList<String> ->
                if (module.path != ":app" && module.classes.any(activityTypes::isActivity)) {
                    violations.add("Activity declarations are only allowed in :app, found in ${module.path}")
                }
            }
        }
    }

    @Test
    fun `launcher activity must only host the app content`() {
        val activityTypes = activityTypes()
        Konture.files {
            that().satisfy { file: FileDeclarationContext ->
                file.modulePath == ":app" &&
                    file.declaration.classes.any(activityTypes::isActivity)
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

    private fun activityTypes(): AndroidActivityTypes = AndroidActivityTypes(
        classes = Konture.projectGraph.getAllModules().flatMap { it.classes }
    )

    private fun mergedManifestViolations(): List<String> = supportedVariants.mapNotNull { variant ->
        val manifest = findProjectRoot().resolve(
            "app/build/intermediates/merged_manifest/$variant/" +
                "process${variant.replaceFirstChar(Char::uppercase)}MainManifest/AndroidManifest.xml"
        )
        val activities = manifest.takeIf(File::exists)?.let(::activityNamesFromMergedManifest)
        if (activities?.size == 1) return@mapNotNull null
        "The $variant merged manifest must declare exactly one Activity; found ${activities?.size ?: "no manifest"}"
    }

    private fun activityNamesFromMergedManifest(manifest: File): Set<String> {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
        val document = documentBuilderFactory.newDocumentBuilder().parse(manifest)
        val activities = document.getElementsByTagName("activity")
        return (0 until activities.length)
            .mapNotNull { index -> activities.item(index).attributes?.getNamedItemNS(androidNamespace, "name")?.nodeValue }
            .filter { name -> name.startsWith("$architectureBasePackage.") }
            .toSet()
    }

    private companion object {
        const val androidNamespace = "http://schemas.android.com/apk/res/android"
        val supportedVariants = listOf("devDebug", "devRelease", "prdDebug", "prdRelease")
    }

}
