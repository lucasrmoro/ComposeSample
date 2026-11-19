#!/usr/bin/env bash
set -euo pipefail

scanner="${1:-gitleaks}"
action_dir="$(dirname -- "${BASH_SOURCE[0]}")"
fixture="$(mktemp -d)"
trap 'rm -rf -- "$fixture"' EXIT

# Build the fixtures outside Git. The fake token exists only inside the generated APK asset.
python3 - "$fixture" <<'PY'
from pathlib import Path
import sys
from zipfile import ZipFile

root = Path(sys.argv[1])
assets = {
    "clean": "catalog_enabled=true\n",
    "secret": "token=" + "glpat-" + "5QXxJ8aP4vH2zK7cM9nR3tW6yB1dF0sA" + "\n",
}
for name, content in assets.items():
    with ZipFile(root / f"{name}.apk", "w") as apk:
        apk.writestr("assets/config.txt", content)
PY

bash "$action_dir/scan-apk-contents.sh" "$scanner" "$fixture/clean.apk" "$fixture/clean"
status=0
bash "$action_dir/scan-apk-contents.sh" "$scanner" "$fixture/secret.apk" "$fixture/secret" \
    > "$fixture/scan.log" 2>&1 || status=$?
if [[ "$status" != 1 ]] || ! grep -Eq 'leaks found: [1-9][0-9]*' "$fixture/scan.log"; then
    cat "$fixture/scan.log" >&2
    printf 'Expected Gitleaks to reject the synthetic secret inside the APK.\n' >&2
    exit 1
fi
printf 'Gitleaks accepted the clean APK and rejected the APK with a synthetic secret.\n'
