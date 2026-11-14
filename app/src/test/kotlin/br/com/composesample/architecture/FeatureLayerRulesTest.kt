package br.com.composesample.architecture

import io.github.baole.konture.Konture
import io.github.baole.konture.modules
import org.junit.Test

class FeatureLayerRulesTest {

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

}
