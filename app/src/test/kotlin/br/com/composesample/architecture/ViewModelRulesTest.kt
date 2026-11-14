package br.com.composesample.architecture

import io.github.baole.konture.ClassDeclaration
import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.Modifier
import io.github.baole.konture.PropertyDeclaration
import io.github.baole.konture.Visibility
import io.github.baole.konture.classes
import io.github.baole.konture.files
import org.junit.Test
import java.io.File

class ViewModelRulesTest {

    @Test
    fun `viewmodels must expose private _uiState as public uiState through asStateFlow`() {
        Konture.classes {
            that().satisfy { declaration: ClassDeclaration ->
                declaration.packageName.hasPackageSegment("viewModel") &&
                    declaration.name.endsWith("ViewModel") &&
                    declaration.filePath.normalizedPath().contains("/features/")
            }
            should().satisfy(
                description = "private and read-only ViewModel state naming",
                predicate = { declaration: ClassDeclaration -> declaration.hasReadOnlyUiStateContract() }
            )
        }
    }

    @Test
    fun `viewmodels must not expose mutable state holders`() {
        Konture.classes {
            that().satisfy { declaration: ClassDeclaration ->
                declaration.packageName.hasPackageSegment("viewModel") &&
                    declaration.name.endsWith("ViewModel") &&
                    declaration.filePath.normalizedPath().contains("/features/")
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
            should().satisfy(
                description = "ViewModel navigation and feature boundary",
                predicate = { file: FileDeclarationContext ->
                    file.declaration.imports.none { import ->
                        import.isNavigationControllerType() || import.isSiblingFeatureImport(file.modulePath)
                    }
                }
            )
        }
    }

    private fun ClassDeclaration.hasReadOnlyUiStateContract(): Boolean {
        val sourceLines = File(filePath).readLines()
        val backingState = properties.firstOrNull { property ->
            property.name == "_uiState" && property.visibility == Visibility.PRIVATE && property.isVal &&
                propertySource(sourceLines, property).contains(mutableStateInitializerPattern)
        }
        val publicState = properties.firstOrNull { property ->
            property.name == "uiState" && property.visibility == Visibility.PUBLIC && property.isVal &&
                Modifier.OVERRIDE in property.modifiers &&
                propertySource(sourceLines, property).contains(readOnlyStateInitializerPattern)
        }
        return backingState != null && publicState != null
    }

    private fun ClassDeclaration.propertySource(sourceLines: List<String>, property: PropertyDeclaration): String {
        if (property.sourceLine <= 0) return ""
        val propertyIndentation = sourceLines.getOrNull(property.sourceLine - 1).orEmpty()
            .takeWhile(Char::isWhitespace)
            .length
        val unparsedMemberLines = sourceLines.mapIndexedNotNull { index, line ->
            val lineIndentation = line.takeWhile(Char::isWhitespace).length
            if (lineIndentation <= propertyIndentation && unparsedMemberDeclarationPattern.containsMatchIn(line)) {
                index + 1
            } else {
                null
            }
        }
        val nextMemberLine = (properties.map { it.sourceLine } + functions.map { it.sourceLine } + unparsedMemberLines)
            .filter { line -> line > property.sourceLine }
            .minOrNull() ?: sourceLines.size + 1
        return sourceLines
            .subList(property.sourceLine - 1, (nextMemberLine - 1).coerceAtMost(sourceLines.size))
            .joinToString(separator = "\n")
            .replace(kotlinBlockCommentPattern, "")
            .replace(kotlinLineCommentPattern, "")
    }

    private companion object {
        val mutableStateInitializerPattern = Regex("\\bMutableStateFlow(?:<[^=]*>)?\\s*\\(")
        val readOnlyStateInitializerPattern = Regex("\\b_uiState\\.asStateFlow\\(\\)")
        val kotlinBlockCommentPattern = Regex("/\\*[\\s\\S]*?\\*/")
        val kotlinLineCommentPattern = Regex("//[^\\r\\n]*")
        val unparsedMemberDeclarationPattern = Regex(
            "^\\s*(?:(?:private|protected|internal|public|open|abstract|sealed|data|enum|" +
                "annotation|value|inner|expect|actual|companion|const|override|final|lateinit|" +
                "suspend|inline|operator|infix|tailrec|external)\\s+)*" +
                "(?:init\\b|class\\b|interface\\b|object\\b|constructor\\b|fun\\b|val\\b|var\\b)"
        )
    }

}
