# Unit tests

The unit-test gate runs `./gradlew :app:testDevDebugUnitTest` on pull requests. It
executes local JVM tests in the app module's `devDebug` test variant; it does not run
instrumented tests.

The app test source set includes the Konture architecture tests. These checks therefore
also run as part of the unit-test gate and through the separate architecture-validation
gate.

To reproduce the gate locally, use JDK 17 and Android SDK 36 and run:

```sh
./gradlew :app:testDevDebugUnitTest
```

When changing another feature or structural module, run that module's corresponding
unit-test task as well if it has local tests.
