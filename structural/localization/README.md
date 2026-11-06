# Localization

`AppRegionProvider` exposes the app resource language, device country and timezone
through `StateFlow`. Android configuration callbacks remain in `impl`.

The app owns the fallback country in
`app/src/main/kotlin/br/com/composesample/configuration/AppConfigurationModule.kt`.
`LocalizationConfiguration` supplies that value when the device locale has no
country; the service does not define a default country.

`AppApplication` explicitly loads `AppConfigurationModule`, then `LocalizationModule`
and calls `AppRegionProvider.start()`. DI registration does not start observation.
