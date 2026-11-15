# launcherActivityHost

Rule method: `ApplicationRulesTest.launcher activity must only host the app content`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Adds one navigation controller import to the launcher Activity.

Expected assertion diagnostic: `launcher Activity host boundary`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.

The real Activity extends ComponentActivity and may omit all navigation imports.
Both cases also contain an ordinary ExampleNavigationActivity with a navigation
import. Its name alone must not classify it as an Android Activity. Only the invalid
case adds the navigation import to the actual ComponentActivity subclass.
