#!/usr/bin/env bash
set -euo pipefail

mapping="app/build/outputs/mapping/${FLAVOR}Release/mapping.txt"
test -s "$mapping"
# Ensure R8 renamed at least one app class, rather than merely producing an empty or identity mapping.
awk '/^br\.com\.composesample\.[^ ]+ -> / { target=$3; sub(/:$/, "", target); if ($1 != target) found=1 } END { exit !found }' "$mapping"

mkdir -p candidate
apk=candidate/unsigned.apk
cp "app/build/outputs/apk/$FLAVOR/release/app-$FLAVOR-release-unsigned.apk" "$apk"
package=br.com.composesample
if [[ "$FLAVOR" == dev ]]; then package+=.dev; fi

[[ "$(apkanalyzer manifest application-id "$apk")" == "$package" ]]
[[ "$(apkanalyzer manifest version-name "$apk")" == "$VERSION" ]]
[[ "$(apkanalyzer manifest version-code "$apk")" == "$VERSION_CODE" ]]
[[ "$(apkanalyzer manifest debuggable "$apk")" == false ]]

if ! command -v xmlstarlet >/dev/null; then
    sudo apt-get update
    sudo apt-get install -y xmlstarlet
fi

# Read the packaged manifest because Gradle merges manifests from app, libraries, and build variants.
manifest="$RUNNER_TEMP/${FLAVOR}-manifest.xml"
apkanalyzer manifest print "$apk" > "$manifest"
xml() { xmlstarlet sel -N a=http://schemas.android.com/apk/res/android -t "$@" "$manifest"; }
[[ "$(xml -v '/manifest/application/@a:usesCleartextTraffic')" == false ]]

# A network security config can override the cleartext flag; reject it along with test-only APKs.
[[ "$(xml -v 'count(/manifest/application[@a:testOnly="true" or @a:networkSecurityConfig] | /manifest/instrumentation)')" == 0 ]]
launcher='count(/manifest/application/*[self::activity or self::activity-alias][@a:exported="true"]/intent-filter[action/@a:name="android.intent.action.MAIN" and category/@a:name="android.intent.category.LAUNCHER"])'
[[ "$(xml -v "$launcher")" == 1 ]]
test_components='count(/manifest/application/*[starts-with(@a:name, "androidx.test.") or starts-with(@a:targetActivity, "androidx.test.")])'
[[ "$(xml -v "$test_components")" == 0 ]]
