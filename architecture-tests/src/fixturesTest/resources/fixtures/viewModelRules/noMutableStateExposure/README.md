# noMutableStateExposure

Rule method: `ViewModelRulesTest.viewmodels must not expose mutable state holders`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Adds one public mutable state property while preserving the required uiState contract.

Expected assertion diagnostic: `read-only ViewModel state`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.
