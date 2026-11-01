# login DI

Centralizes the feature's DI registrations without `api` and `impl` submodules.
The app depends on `:features:login:di` and calls `LoginModule.init()` during
startup, after loading the structural DI modules.

Consumers use feature contracts directly; no intermediate interface loads DI.
Bindings remain in this module. Presentation, domain, and data modules do not depend
on it.

This module also registers `LoginAnalytics`, defined in `../analytics`, for injection
into the feature's layers. Load `AnalyticsModule.init()` before first resolving
that contract.
