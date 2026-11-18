#!/usr/bin/env bash
set -euo pipefail

scan="$(mktemp -d)"
trap 'rm -rf -- "$scan"' EXIT
curl --fail --silent --show-error --location \
    https://github.com/gitleaks/gitleaks/releases/download/v8.30.1/gitleaks_8.30.1_linux_x64.tar.gz \
    --output "$scan/gitleaks.tar.gz"
echo "551f6fc83ea457d62a0d98237cbad105af8d557003051f41f3e7ca7b3f2470eb  $scan/gitleaks.tar.gz" | sha256sum --check --status
tar -xzf "$scan/gitleaks.tar.gz" -C "$scan" gitleaks

# Scan the squash commit against its main parent and redact any findings in workflow logs.
"$scan/gitleaks" git --redact=100 --log-opts="HEAD^1..HEAD"
"$scan/gitleaks" dir --redact=100 --max-decode-depth=2 --max-archive-depth=1 candidate
