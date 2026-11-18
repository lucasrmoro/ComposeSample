#!/usr/bin/env bash
set -euo pipefail

mkdir -p candidate
printf '# Version %s\n\n- %s\n' "$VERSION" "$(git show -s --format=%s HEAD)" > candidate/RELEASE_NOTES.md
jq -n --arg sha "$(git rev-parse HEAD)" --arg version "$VERSION" --argjson code "$VERSION_CODE" \
    --arg flavor "$FLAVOR" '{sourceCommit:$sha,versionName:$version,versionCode:$code,flavor:$flavor}' > candidate/release.json
