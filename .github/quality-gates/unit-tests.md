# Unit tests

The unit-test gate runs `./gradlew :app:testDevDebugUnitTest` on pull requests. It
executes local JVM tests in the app module's `devDebug` test variant; it does not run
instrumented tests.

Konture architecture tests live in `:architecture-tests` and run through the separate
architecture-validation gate. The app unit-test task may report `NO-SOURCE` when the
app has no local JVM tests; compilation of its dependencies still runs as required.

To reproduce the gate locally, use JDK 17 and Android SDK 36 and run:

```sh
./gradlew :app:testDevDebugUnitTest
```

When changing another feature or structural module, run that module's corresponding
unit-test task as well if it has local tests.
