# injectedDispatchers

Rule method: `CoroutineRulesTest.hard coded coroutine dispatchers must stay in the dispatcher provider`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Imports Dispatchers.IO directly in a production implementation, exercising member imports as well as the Dispatchers namespace restriction.

Expected assertion diagnostic: `injected coroutine dispatchers`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.
