# readOnlyStateContract

Rule method: `ViewModelRulesTest.viewmodels must expose private _uiState as public uiState through asStateFlow`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Changes only the backing state from val to var; public state remains read-only.

Expected assertion diagnostic: `private and read-only ViewModel state naming`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.
