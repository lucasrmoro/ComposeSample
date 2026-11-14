package br.com.composesample.architecture

import io.github.baole.konture.Konture
import io.github.baole.konture.modules
import org.junit.Test

class LayerRulesTest {

    @Test
    fun `feature data modules must not depend on presentation or domain implementations`() {
        Konture.modules {
            that().haveNameMatching(":features:*:data")
            should().notDependOn(":features:*:presentation", ":features:*:domain:impl", ":features:*:di")
        }
    }

    @Test
    fun `feature domain api modules must not depend on outer feature layers`() {
        Konture.modules {
            that().haveNameMatching(":features:*:domain:api")
            should().notDependOn(
                ":features:*:data",
                ":features:*:di",
                ":features:*:domain:impl",
                ":features:*:presentation"
            )
        }
    }

    @Test
    fun `feature domain implementations must not depend on data presentation or di`() {
        Konture.modules {
            that().haveNameMatching(":features:*:domain:impl")
            should().notDependOn(
                ":features:*:data",
                ":features:*:di",
                ":features:*:presentation"
            )
        }
    }

    @Test
    fun `feature presentation modules must depend on contracts instead of implementations`() {
        Konture.modules {
            that().haveNameMatching(":features:*:presentation")
            should().notDependOn(
                ":features:*:data",
                ":features:*:di",
                ":features:*:domain:impl"
            )
        }
    }

    @Test
    fun `structural modules must not depend on feature modules`() {
        Konture.modules {
            that().haveNameMatching(":structural:**")
            should().satisfy { module, violations ->
                module.projectDependencyPaths(includeTestDependencies = false)
                    .filter { dependency -> dependency.startsWith(":features:") }
                    .forEach { dependency ->
                        violations.add("${module.path} depends on feature module $dependency")
                    }
            }
        }
    }

    @Test
    fun `structural api and implementation modules must not depend on di modules`() {
        Konture.modules {
            that().matching { module ->
                module.path.startsWith(":structural:") &&
                    (module.path.endsWith(":api") || module.path.endsWith(":impl"))
            }
            should().satisfy { module, violations ->
                module.projectDependencyPaths(includeTestDependencies = false)
                    .filter { dependency -> dependency.matches(structuralDiModulePathPattern) }
                    .forEach { dependency ->
                        violations.add("${module.path} depends on DI module $dependency")
                    }
            }
        }
    }

    @Test
    fun `structural implementations may be consumed only by their service di module or app`() {
        Konture.modules {
            that().matching { true }
            should().satisfy { module, violations ->
                module.projectDependencyPaths(includeTestDependencies = false)
                    .filter { dependency -> dependency.matches(structuralImplModulePathPattern) }
                    .filterNot { dependency ->
                        module.path == ":app" || module.path == dependency.removeSuffix(":impl") + ":di"
                    }
                    .forEach { dependency ->
                        violations.add("${module.path} depends directly on implementation module $dependency")
                    }
            }
        }
    }

}
