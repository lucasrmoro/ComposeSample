package br.com.composesample.architecture

import io.github.baole.konture.FileDeclarationContext
import io.github.baole.konture.Konture
import io.github.baole.konture.files
import org.junit.Test

class CoroutineRulesTest {

    @Test
    fun `production code must not launch coroutines in global scope`() {
        Konture.files {
            that().satisfy { _: FileDeclarationContext -> true }
            should().satisfy(description = "coroutine lifecycle ownership", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.none { import -> import == "kotlinx.coroutines.GlobalScope" } &&
                    file.declaration.usages.none { usage -> usage.targetFqName == "kotlinx.coroutines.GlobalScope" }
            })
        }
    }

    @Test
    fun `hard coded coroutine dispatchers must stay in the dispatcher provider`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.modulePath != ":structural:core:di" }
            should().satisfy(description = "injected coroutine dispatchers", predicate = { file: FileDeclarationContext ->
                file.declaration.imports.none { import -> import == "kotlinx.coroutines.Dispatchers" } &&
                    file.declaration.usages.none { usage -> usage.targetFqName == "kotlinx.coroutines.Dispatchers" }
            })
        }
    }

}
