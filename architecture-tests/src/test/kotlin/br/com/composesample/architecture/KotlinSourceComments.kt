package br.com.composesample.architecture

internal fun String.maskKotlinCommentsPreservingLineNumbers(): String = buildString(length) {
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
