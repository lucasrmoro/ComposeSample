# centralizedDispatcherCreation

Rule method: `CoroutineRulesTest.coroutine dispatcher factories must stay in the dispatcher provider`.

The valid example keeps newSingleThreadContext in :structural:core:di and injects CoroutineDispatcher into the consumer. The invalid example creates the dispatcher directly in a consumer.

Both cases execute only this method. Expected assertion diagnostic: `centralized coroutine dispatcher creation`.
Technical TestKit setup adds no example sources; the Kotlin sources are analyzed without compiling their SDK dependencies.
