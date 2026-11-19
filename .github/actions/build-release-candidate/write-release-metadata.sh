#!/usr/bin/env bash
set -euo pipefail

mkdir -p candidate
# Keep public notes tied to the reviewed squash title and the exact source revision being built.
printf '# Version %s\n\n- %s\n' "$VERSION" "$(git show -s --format=%s HEAD)" > candidate/RELEASE_NOTES.md
# This small JSON file travels beside the APK so signing and publishing can verify its source/version.
jq -n --arg sha "$(git rev-parse HEAD)" --arg version "$VERSION" --argjson code "$VERSION_CODE" \
    --arg flavor "$FLAVOR" '{sourceCommit:$sha,versionName:$version,versionCode:$code,flavor:$flavor}' > candidate/release.json
