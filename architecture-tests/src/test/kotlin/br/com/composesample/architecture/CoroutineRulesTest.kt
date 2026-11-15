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
                !file.referencesCoroutineApi("kotlinx.coroutines.GlobalScope")
            })
        }
    }

    @Test
    fun `hard coded coroutine dispatchers must stay in the dispatcher provider`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.modulePath != ":structural:core:di" }
            should().satisfy(description = "injected coroutine dispatchers", predicate = { file: FileDeclarationContext ->
                !file.referencesCoroutineApi("kotlinx.coroutines.Dispatchers")
            })
        }
    }

    @Test
    fun `production code must not create main scope with an implicit dispatcher`() {
        Konture.files {
            that().satisfy { _: FileDeclarationContext -> true }
            should().satisfy(description = "explicit coroutine scope context", predicate = { file: FileDeclarationContext ->
                !file.referencesCoroutineApi("kotlinx.coroutines.MainScope")
            })
        }
    }

    @Test
    fun `coroutine dispatcher factories must stay in the dispatcher provider`() {
        Konture.files {
            that().satisfy { file: FileDeclarationContext -> file.modulePath != ":structural:core:di" }
            should().satisfy(description = "centralized coroutine dispatcher creation", predicate = { file: FileDeclarationContext ->
                dispatcherFactoryApis.none { api -> file.referencesCoroutineApi(api) }
            })
        }
    }

    private fun FileDeclarationContext.referencesCoroutineApi(api: String): Boolean =
        declaration.imports.any { import -> import == api || import.startsWith("$api.") } ||
            declaration.usages.any { usage ->
                usage.targetFqName == api || usage.targetFqName.startsWith("$api.")
            }

    private companion object {
        val dispatcherFactoryApis = setOf(
            "kotlinx.coroutines.newSingleThreadContext",
            "kotlinx.coroutines.newFixedThreadPoolContext",
            "kotlinx.coroutines.asCoroutineDispatcher",
            "kotlinx.coroutines.android.asCoroutineDispatcher"
        )
    }

}
