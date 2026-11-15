# exactlyOneActivity

Rule method: `ApplicationRulesTest.the project must declare exactly one activity in the app module`.

Both cases contain only their own required modules and source examples. Each executes only this method.

The `invalid` case introduces one targeted change: Adds one extra Activity in the app module.

Expected assertion diagnostic: `exactly one Activity`.

Each case is copied directly to its own temporary project. Technical TestKit setup is provided by the executor, without adding modules or example sources. Kotlin declarations and imports are analyzed by Konture; fixture production code is not compiled and imported SDK types do not need runtime stubs. Some architectural boundaries overlap; this pair isolates the selected method.

The valid Activity extends AppCompatActivity. The invalid case adds a second Activity
extending ComponentActivity. Classification uses Android types, not navigation imports
or class-name suffixes. The supplied manifest is the same in both cases.
