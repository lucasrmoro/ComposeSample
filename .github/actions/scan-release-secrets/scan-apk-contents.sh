#!/usr/bin/env bash
set -euo pipefail

scanner="$1"
apk="$2"
destination="$3"

# Gitleaks skips APK containers, so unpack assets and resources before scanning them.
python3 "$(dirname -- "${BASH_SOURCE[0]}")/extract-apk.py" "$apk" "$destination"
"$scanner" dir --redact=100 --max-decode-depth=2 --max-archive-depth=1 "$destination"
