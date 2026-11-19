#!/usr/bin/env bash
set -euo pipefail

mapping="app/build/outputs/mapping/${FLAVOR}Release/mapping.txt"
test -s "$mapping"
# Read the compiler that produced this APK's mapping, since AGP also bundles an older R8.
r8_version="$(sed -n 's/^# compiler_version: //p' "$mapping")"
if [[ ! "$r8_version" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
    printf 'Missing or unexpected R8 compiler version in %s: %s\n' "$mapping" "$r8_version" >&2
    exit 1
fi
major="${BASH_REMATCH[1]}"
minor="${BASH_REMATCH[2]}"
patch="${BASH_REMATCH[3]}"
if (( major < 9 || (major == 9 && (minor < 1 || (minor == 1 && patch < 29))) )); then
    printf 'Kotlin 2.4 requires R8 9.1.29 or newer, but the release used %s.\n' "$r8_version" >&2
    exit 1
fi
printf 'R8 compiler version: %s\n' "$r8_version"

# R8 writes original-name -> obfuscated-name rows here; require at least one app class to change name.
awk '/^br\.com\.composesample\.[^ ]+ -> / { target=$3; sub(/:$/, "", target); if ($1 != target) found=1 } END { exit !found }' "$mapping"

# Copy the APK to a stable path used by the following scans and artifact upload.
mkdir -p candidate
apk=candidate/unsigned.apk
cp "app/build/outputs/apk/$FLAVOR/release/app-$FLAVOR-release-unsigned.apk" "$apk"
package=br.com.composesample
if [[ "$FLAVOR" == dev ]]; then package+=.dev; fi

[[ "$(apkanalyzer manifest application-id "$apk")" == "$package" ]]
[[ "$(apkanalyzer manifest version-name "$apk")" == "$VERSION" ]]
[[ "$(apkanalyzer manifest version-code "$apk")" == "$VERSION_CODE" ]]
# Release manifests must not be debuggable, regardless of which flavor produced the APK.
[[ "$(apkanalyzer manifest debuggable "$apk")" == false ]]

if ! command -v xmlstarlet >/dev/null; then
    sudo apt-get update
    sudo apt-get install -y xmlstarlet
fi

# Inspect the packaged manifest: Gradle has merged declarations from the app, libraries, and flavor.
manifest="$RUNNER_TEMP/${FLAVOR}-manifest.xml"
apkanalyzer manifest print "$apk" > "$manifest"
# xmlstarlet evaluates Android-namespace XPath queries against the decoded manifest.
xml() { xmlstarlet sel -N a=http://schemas.android.com/apk/res/android -t "$@" "$manifest"; }
[[ "$(xml -v '/manifest/application/@a:usesCleartextTraffic')" == false ]]

# A network security config can override the cleartext flag; reject it along with test-only APKs.
[[ "$(xml -v 'count(/manifest/application[@a:testOnly="true" or @a:networkSecurityConfig] | /manifest/instrumentation)')" == 0 ]]
# XPath counts launcher intent filters after manifest merging, including activity aliases.
launcher='count(/manifest/application/*[self::activity or self::activity-alias][@a:exported="true"]/intent-filter[action/@a:name="android.intent.action.MAIN" and category/@a:name="android.intent.category.LAUNCHER"])'
# A launcher must be exported so Android can start it, and the APK must have exactly one launcher entry.
[[ "$(xml -v "$launcher")" == 1 ]]
test_components='count(/manifest/application/*[starts-with(@a:name, "androidx.test.") or starts-with(@a:targetActivity, "androidx.test.")])'
# Test APK components should never be merged into a distributable release.
[[ "$(xml -v "$test_components")" == 0 ]]
