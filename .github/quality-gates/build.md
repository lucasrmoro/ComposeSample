# Build

The build gate runs `./gradlew :app:assembleDevDebug` on pull requests. It verifies that
the `devDebug` app variant and its required modules can be assembled into an APK.

CI uses JDK 17 and Android SDK 36. To reproduce the gate locally, use the same JDK and
SDK versions and run:

```sh
./gradlew :app:assembleDevDebug
```

Fix compilation, resource processing, dependency resolution, or packaging failures in
the affected module. This gate builds the dev debug variant; it does not build or test
the `prd` flavor.
