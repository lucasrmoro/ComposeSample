# Session

`session/api` exposes `AppSessionProvider` and `SessionStatus`. The implementation
maintains session expiration state, observed by the application state aggregator in `core`.

The app depends on `session/di` and loads `SessionModule.init()` before using the
contracts. The DI module registers a singleton `AppSessionProvider` through the
factory function in `session/impl`. Its implementation remains internal.

Consumers depend on `api`. Neither the API nor the implementation depends on the
DI module; DI registrations belong exclusively there. `expireSession()` publishes
expiration to existing observers.
