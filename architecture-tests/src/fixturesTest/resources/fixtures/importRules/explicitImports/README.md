# explicitImports

Rule method: `ImportRulesTest.production Kotlin source must not use import aliases or wildcard imports`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Adds one wildcard import to a data source file.

Expected assertion diagnostic: `explicit imports`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.
