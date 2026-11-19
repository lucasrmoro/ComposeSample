#!/usr/bin/env bash
set -euo pipefail

scan="$(mktemp -d)"
trap 'rm -rf -- "$scan"' EXIT
# Download a fixed Gitleaks release and verify its published archive digest before executing it.
curl --fail --silent --show-error --location \
    https://github.com/gitleaks/gitleaks/releases/download/v8.30.1/gitleaks_8.30.1_linux_x64.tar.gz \
    --output "$scan/gitleaks.tar.gz"
echo "551f6fc83ea457d62a0d98237cbad105af8d557003051f41f3e7ca7b3f2470eb  $scan/gitleaks.tar.gz" | sha256sum --check --status
tar -xzf "$scan/gitleaks.tar.gz" -C "$scan" gitleaks

# Compare the merged squash commit with its first parent; redact matched secrets in Actions logs.
"$scan/gitleaks" git --redact=100 --log-opts="HEAD^1..HEAD"
# Scan the release staging files, then safely unpack the APK because Gitleaks does not identify .apk as a ZIP archive.
"$scan/gitleaks" dir --redact=100 --max-decode-depth=2 --max-archive-depth=1 candidate
bash "$GITHUB_ACTION_PATH/scan-apk-contents.sh" "$scan/gitleaks" candidate/unsigned.apk "$scan/apk"
