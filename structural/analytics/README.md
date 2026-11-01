# Analytics

Encapsulates event logging through Firebase Analytics. Consumers depend on
`analytics/api`; SDK types and `Bundle` remain in `impl`. `analytics/di` registers
`AnalyticsProvider` as a singleton. `AppApplication` calls `initializeFirebase(this)`
and `initializeAnalytics(this)` explicitly, then loads `AnalyticsModule.init()` before feature DI modules.
The DI module only registers bindings; the factory starts no background work.

## Usage

Each feature has an `analytics` module with typed methods. Its implementation
translates calls into `AnalyticsEvent`, which accepts `vararg Pair<String, String>`.
All parameter values are sent as text. Consuming layers do not build maps or know
event names.

```kotlin
class SessionRepository(private val analytics: LoginAnalytics) {

    fun notifyRefreshStarted() {
        analytics.tokenRefreshStarted()
    }

}
```

`track()` returns `UNAVAILABLE` without configuration and `ACCEPTED` when the call
has been forwarded to the SDK. Acceptance does not confirm network delivery or
presence in reports. The structural service does not retain events while unavailable.

Event and parameter names follow `[A-Za-z][A-Za-z0-9_]{0,39}` and cannot start with
`firebase_`, `google_`, or `ga_`. Events support up to 25 parameters. Invalid names,
excess parameters, and duplicate parameter names cause `IllegalArgumentException`
when constructing `AnalyticsEvent`. Do not include passwords, tokens, or personal
identifiers in parameters.

Construct events inside the feature analytics implementation:

```kotlin
AnalyticsEvent(
    "feature_a_list_load_finished",
    "page" to page.toString(),
    "item_count" to itemCount.toString()
)
```

## Android permissions

`analytics/impl` declares `INTERNET`, `ACCESS_NETWORK_STATE`, and `WAKE_LOCK`, required
by `FirebaseAnalytics.getInstance()`. These are normal manifest permissions and do
not require runtime prompts. Manifest merging includes them in consuming apps.

## Configure Firebase later

SDK dependencies use the BoM declared in `gradle/libs.versions.toml`, exclusively
in `impl`. BoM 34.0.0 supports the project's Kotlin 2.1 toolchain; verify compatibility
before upgrading. The project includes no placeholder Firebase configuration.
Without Firebase resources, the app starts normally and the provider remains unavailable.

To connect a project:

1. Register the application IDs for the variants used in the Firebase console,
   including the `.debug` suffix for debug builds.
2. Add the `com.google.gms.google-services` plugin to the version catalog and apply
   it in the `app` module.
3. Place `google-services.json` in `app/` or the corresponding source sets, such as
   `app/src/dev/` and `app/src/prd/`, with clients for the variants used.
4. Rebuild and restart the app. DI constructs each provider once, so configuration
   must exist before its first resolution.

The plugin turns the configuration file into resources read by `initializeFirebase()`.
The app removes `FirebaseInitProvider` from its merged manifest so initialization
runs explicitly in `AppApplication.onCreate()`. `initializeAnalytics()` then prepares
the analytics SDK. Provider factories only access the already initialized default
Firebase app. This configuration is shared with [Remote Config](../remote-config/README.md).

References: [Android setup](https://firebase.google.com/docs/android/setup) and
[event logging](https://firebase.google.com/docs/analytics/android/events).
