# Android Lint

The lint gate runs `./gradlew :app:lintDevDebug` on pull requests. Android Lint analyzes
the app's `devDebug` variant and its Android project inputs for issues detectable by the
configured Android Lint checks, including source, resources, and manifest problems.

To reproduce the gate locally, use JDK 17 and Android SDK 36 and run:

```sh
./gradlew :app:lintDevDebug
```

Review the reported file and line, then fix the issue or update the relevant lint
configuration when the finding is intentionally inapplicable. This gate is Android
Lint; it does not currently run Detekt or ktlint.
