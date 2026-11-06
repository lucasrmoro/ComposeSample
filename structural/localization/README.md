# Localization

`AppRegionProvider` exposes the app resource language, device region (BR when absent), and timezone through StateFlow. Load `LocalizationModule` and call `start()` explicitly in AppApplication. Platform configuration callbacks remain in impl; registration does not start in a DI factory.
