# featureRoutePrefix

Rule method: `NavigationRulesTest.feature route declarations must use the feature prefix`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Adds one route whose name lacks the Example feature prefix; shared contracts remain present.

Expected assertion diagnostic: `feature route naming`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.
