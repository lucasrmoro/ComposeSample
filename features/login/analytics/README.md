# login analytics

Exposes `LoginAnalytics` to the feature's layers through `tokenRefreshStarted()` and `tokenRefreshFinished(succeeded)`.
The internal implementation depends only on `structural/analytics/api`.
`LoginModule.init()` registers the contract as a singleton through
`createLoginAnalytics()`. This module declares no DI bindings.

The login repository receives the contract through its constructor. Other feature layers
can depend on this module to consume the same methods without knowing event names,
building maps, or depending on Firebase. This module does not depend on those layers.

Methods return `AnalyticsResult`: unavailable integration differs from a call accepted
by the SDK. The result does not change the business flow. Payloads contain no tokens,
passwords, or personal identifiers.
