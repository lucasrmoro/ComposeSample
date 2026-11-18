#!/usr/bin/env bash
set -euo pipefail

case "$FLAVOR" in
    dev) variant=DevRelease ;;
    prd) variant=PrdRelease ;;
    *) echo "Unsupported release flavor: $FLAVOR"; exit 2 ;;
esac

# R8 exhausted the default heap while shrinking devRelease.
./gradlew ":app:assemble$variant" ":app:lint$variant" ":app:test${variant}UnitTest" \
    :app:writeReleaseInventory "-PreleaseFlavor=$FLAVOR" \
    '-Dorg.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8' \
    "-PappReleaseVersion=$VERSION" "-PappReleaseCode=$VERSION_CODE"
