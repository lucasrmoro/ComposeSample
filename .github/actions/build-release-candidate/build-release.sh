#!/usr/bin/env bash
set -euo pipefail

case "$FLAVOR" in
    dev) variant=DevRelease ;;
    prd) variant=PrdRelease ;;
    *) echo "Unsupported release flavor: $FLAVOR"; exit 2 ;;
esac

# Release builds shrink and obfuscate code with R8, which needs more heap than the default runner setting.
# The explicit Gradle properties make the version shown by Android match the reviewed release calculation.
./gradlew ":app:assemble$variant" ":app:lint$variant" ":app:test${variant}UnitTest" \
    :app:writeReleaseInventory "-PreleaseFlavor=$FLAVOR" \
    '-Dorg.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8' \
    "-PappReleaseVersion=$VERSION" "-PappReleaseCode=$VERSION_CODE"
