# Session

`session/api` exposes `AppSessionProvider` and `SessionStatus`. The implementation
maintains session expiration state, observed by the application state aggregator in `core`.

The app depends on `session/di` and loads `SessionModule.init()` before using the
contracts. The DI module registers a singleton `AppSessionProvider` through the
factory function in `session/impl`. Its implementation remains internal.

Consumers depend on `api`. Neither the API nor the implementation depends on the
DI module; DI registrations belong exclusively there. `expireSession()` publishes
expiration synchronously through `isSessionExpired: StateFlow<Boolean>`, initialized
to false. Existing collectors receive updates; new collectors receive the current
value, including expiration that happened before collection. Repeated expiration
calls keep the value true without duplicate emissions. The provider starts no
background tasks and requires no lifecycle or LiveData dependency.

Consumers migrate from LiveData observation to Flow collection. `AppStateProvider`
combines this state with network connectivity without an intermediate LiveData adapter.
