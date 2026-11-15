# explicitScopeContext

Rule method: `CoroutineRulesTest.production code must not create main scope with an implicit dispatcher`.

The valid example constructs CoroutineScope with a constructor-injected dispatcher and SupervisorJob. The invalid example uses a fully qualified MainScope call, which embeds Dispatchers.Main.

Both cases execute only this method. Expected assertion diagnostic: `explicit coroutine scope context`.
Technical TestKit setup adds no example sources; the Kotlin sources are analyzed without compiling their SDK dependencies.
