#!/usr/bin/env bash
set -euo pipefail

scanner="$1"
apk="$2"
destination="$3"
sdk_tools="${ANDROID_HOME:?Set ANDROID_HOME to the Android SDK directory}/build-tools/36.0.0"

# Gitleaks skips APK containers, so unpack assets and resources before scanning them.
python3 "$(dirname -- "${BASH_SOURCE[0]}")/extract-apk.py" "$apk" "$destination/apk"

# DEX bytecode is binary. Export each DEX string table as text for Gitleaks.
find "$destination/apk" -type f -name '*.dex' -print0 | while IFS= read -r -d '' dex; do
    relative="${dex#"$destination/apk/"}"
    output="$destination/dex-strings/$relative.txt"
    mkdir -p "$(dirname -- "$output")"
    "$sdk_tools/dexdump" -s "$dex" > "$output"
done

"$scanner" dir --redact=100 --max-decode-depth=2 --max-archive-depth=1 "$destination"
