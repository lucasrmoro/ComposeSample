# noDiDependencies

Rule method: `StructuralLayerRulesTest.structural api and implementation modules must not depend on di modules`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Adds the single forbidden edge `:structural:media:api → :structural:design-system:di`.

Expected assertion diagnostic: `depends on DI module`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.
