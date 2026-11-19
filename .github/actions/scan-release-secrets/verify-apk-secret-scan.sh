#!/usr/bin/env bash
set -euo pipefail

scanner="${1:-gitleaks}"
action_dir="$(dirname -- "${BASH_SOURCE[0]}")"
d8="${ANDROID_HOME:?Set ANDROID_HOME to the Android SDK directory}/build-tools/36.0.0/d8"
fixture="$(mktemp -d)"
trap 'rm -rf -- "$fixture"' EXIT

# Build both clean and leaking fixtures outside Git and compile real DEX with D8.
python3 - "$fixture" "$d8" <<'PY'
import subprocess
import sys
from pathlib import Path
from zipfile import ZipFile

root = Path(sys.argv[1])
d8 = sys.argv[2]
clean = "catalog_enabled=true"
secret = "token=" + "glpat-" + "5QXxJ8aP4vH2zK7cM9nR3tW6yB1dF0sA"

for label, content in (("clean", clean), ("secret", secret)):
    with ZipFile(root / f"asset-{label}.apk", "w") as apk:
        apk.writestr("assets/config.txt", content + "\n")

    source = root / "DexFixture.java"
    source.write_text(
        f'public class DexFixture {{ public String value() {{ return "{content}"; }} }}\n',
        encoding="utf-8",
    )
    subprocess.run(["javac", "--release", "8", str(source)], check=True)
    subprocess.run([d8, "--min-api", "26", "--output", str(root), str(source.with_suffix(".class"))], check=True)
    with ZipFile(root / f"dex-{label}.apk", "w") as apk:
        apk.write(root / "classes.dex", "classes.dex")
PY

for content_type in asset dex; do
    bash "$action_dir/scan-apk-contents.sh" "$scanner" \
        "$fixture/$content_type-clean.apk" "$fixture/$content_type-clean"
    status=0
    bash "$action_dir/scan-apk-contents.sh" "$scanner" \
        "$fixture/$content_type-secret.apk" "$fixture/$content_type-secret" \
        > "$fixture/scan.log" 2>&1 || status=$?
    if [[ "$status" != 1 ]] || ! grep -Eq 'leaks found: [1-9][0-9]*' "$fixture/scan.log"; then
        cat "$fixture/scan.log" >&2
        printf 'Expected Gitleaks to reject the synthetic secret in the %s fixture.\n' "$content_type" >&2
        exit 1
    fi
    printf 'Gitleaks accepted the clean %s fixture and rejected the one with a synthetic secret.\n' "$content_type"
done
