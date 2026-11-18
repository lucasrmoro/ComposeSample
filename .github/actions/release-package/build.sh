#!/usr/bin/env bash
set -euo pipefail
case "$FLAVOR" in
    dev) variant=DevRelease ;;
    prd) variant=PrdRelease ;;
    *) exit 2 ;;
esac

./gradlew ":app:assemble$variant" ":app:lint$variant" ":app:test${variant}UnitTest" \
    :app:writeReleaseInventory "-PreleaseFlavor=$FLAVOR"
if [[ "$FLAVOR" == prd ]]; then
    ./gradlew architectureCheck
fi
mapping="app/build/outputs/mapping/${FLAVOR}Release/mapping.txt"
test -s "$mapping"
# R8's mapping must include at least one renamed application class.
awk '/^br\.com\.composesample\.[^ ]+ -> / { target=$3; sub(/:$/, "", target); if ($1 != target) found=1 } END { exit !found }' "$mapping"
mkdir -p candidate
cp "app/build/outputs/apk/$FLAVOR/release/app-$FLAVOR-release-unsigned.apk" candidate/unsigned.apk
python3 "$GITHUB_ACTION_PATH/inspect-apk.py" candidate/unsigned.apk "$VERSION" "$VERSION_CODE" "$FLAVOR"
python3 "$GITHUB_ACTION_PATH/audit.py" "app/build/reports/release/$FLAVOR-dependencies.json" candidate/sbom.cdx.json

scan="$(mktemp -d)"
trap 'rm -rf -- "$scan"' EXIT
curl --fail --silent --show-error --location \
    https://github.com/gitleaks/gitleaks/releases/download/v8.30.1/gitleaks_8.30.1_linux_x64.tar.gz \
    --output "$scan/gitleaks.tar.gz"
echo "551f6fc83ea457d62a0d98237cbad105af8d557003051f41f3e7ca7b3f2470eb  $scan/gitleaks.tar.gz" | sha256sum --check --status
tar -xzf "$scan/gitleaks.tar.gz" -C "$scan" gitleaks
"$scan/gitleaks" git --redact=100 --log-opts="HEAD^1..HEAD"
"$scan/gitleaks" dir --redact=100 --max-decode-depth=2 --max-archive-depth=1 candidate
cp RELEASE_NOTES.md candidate/
jq -n --arg sha "$(git rev-parse HEAD)" --arg version "$VERSION" --argjson code "$VERSION_CODE" \
    --arg flavor "$FLAVOR" '{sourceCommit:$sha,versionName:$version,versionCode:$code,flavor:$flavor}' > candidate/release.json
