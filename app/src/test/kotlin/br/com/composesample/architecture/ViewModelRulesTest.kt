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
        val commentMaskedLines = sourceLines.joinToString(separator = "\n")
            .maskKotlinCommentsPreservingLineNumbers()
            .lines()
        val propertyIndentation = commentMaskedLines.getOrNull(property.sourceLine - 1).orEmpty()
            .takeWhile(Char::isWhitespace)
            .length
        val unparsedMemberLines = commentMaskedLines.mapIndexedNotNull { index, line ->
            val lineIndentation = line.takeWhile(Char::isWhitespace).length
            if (lineIndentation <= propertyIndentation && unparsedMemberDeclarationPattern.containsMatchIn(line)) {
                index + 1
            } else {
                null
            }
        }
        val nextMemberLine = (properties.map { it.sourceLine } + functions.map { it.sourceLine } + unparsedMemberLines)
            .filter { line -> line > property.sourceLine }
            .minOrNull() ?: commentMaskedLines.size + 1
        return commentMaskedLines
            .subList(property.sourceLine - 1, (nextMemberLine - 1).coerceAtMost(sourceLines.size))
            .joinToString(separator = "\n")
    }

    private fun String.maskKotlinCommentsPreservingLineNumbers(): String = buildString(length) {
        var index = 0
        var blockCommentDepth = 0
        var inLineComment = false
        var inString = false
        var inMultilineString = false
        var inCharacter = false
        var isEscaped = false

        while (index < this@maskKotlinCommentsPreservingLineNumbers.length) {
            val character = this@maskKotlinCommentsPreservingLineNumbers[index]
            val nextCharacter = this@maskKotlinCommentsPreservingLineNumbers.getOrNull(index + 1)
            val thirdCharacter = this@maskKotlinCommentsPreservingLineNumbers.getOrNull(index + 2)

            when {
                blockCommentDepth > 0 && character == '/' && nextCharacter == '*' -> {
                    append("  ")
                    blockCommentDepth++
                    index += 2
                }
                blockCommentDepth > 0 && character == '*' && nextCharacter == '/' -> {
                    append("  ")
                    blockCommentDepth--
                    index += 2
                }
                blockCommentDepth > 0 -> {
                    append(if (character == '\n' || character == '\r') character else ' ')
                    index++
                }
                inLineComment && character != '\n' && character != '\r' -> {
                    append(' ')
                    index++
                }
                inLineComment -> {
                    append(character)
                    inLineComment = false
                    index++
                }
                inString || inCharacter -> {
                    append(character)
                    when {
                        inString && inMultilineString && character == '"' && nextCharacter == '"' && thirdCharacter == '"' -> {
                            append("\"\"")
                            inString = false
                            inMultilineString = false
                            index += 2
                        }
                        inString && inMultilineString -> Unit
                        isEscaped -> isEscaped = false
                        character == '\\' -> isEscaped = true
                        inCharacter && character == '\'' -> inCharacter = false
                        inString && !inMultilineString && character == '"' -> inString = false
                    }
                    index++
                }
                character == '/' && nextCharacter == '/' -> {
                    append("  ")
                    inLineComment = true
                    index += 2
                }
                character == '/' && nextCharacter == '*' -> {
                    append("  ")
                    blockCommentDepth = 1
                    index += 2
                }
                character == '"' && nextCharacter == '"' && thirdCharacter == '"' -> {
                    append("\"\"\"")
                    inString = true
                    inMultilineString = true
                    index += 3
                }
                character == '"' -> {
                    append(character)
                    inString = true
                    index++
                }
                character == '\'' -> {
                    append(character)
                    inCharacter = true
                    index++
                }
                else -> {
                    append(character)
                    index++
                }
            }
        }
    }

    private companion object {
        val mutableStateInitializerPattern = Regex(
            "(?s)^\\s*private\\s+val\\s+_uiState\\b[^=]*=\\s*MutableStateFlow(?:<[^=]*>)?\\s*\\("
        )
        val readOnlyStateInitializerPattern = Regex(
            "(?s)^\\s*(?:(?:public|override)\\s+)*val\\s+uiState\\b[^=]*=\\s*_uiState\\.asStateFlow\\(\\)"
        )
        val unparsedMemberDeclarationPattern = Regex(
            "^\\s*(?:(?:private|protected|internal|public|open|abstract|sealed|data|enum|" +
                "annotation|value|inner|expect|actual|companion|const|override|final|lateinit|" +
                "suspend|inline|operator|infix|tailrec|external)\\s+)*" +
                "(?:init\\b|class\\b|interface\\b|object\\b|constructor\\b|fun\\b|val\\b|var\\b)"
        )
    }

}
