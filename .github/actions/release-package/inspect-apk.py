"""Check the packaged production manifest against the reviewed release policy."""

from pathlib import Path
import subprocess
import sys
import xml.etree.ElementTree as ET

ANDROID = "{http://schemas.android.com/apk/res/android}"
LAUNCHER = "br.com.composesample.activity.LauncherActivity"


def require(condition, message):
    if not condition:
        raise SystemExit(message)


def main():
    apk, version, code, flavor = sys.argv[1:]
    package = "br.com.composesample.dev" if flavor == "dev" else "br.com.composesample"
    policy = Path(__file__).parent
    manifest = ET.fromstring(subprocess.check_output(["apkanalyzer", "manifest", "print", apk]))
    application = manifest.find("application")
    require(manifest.get("package") == package, "Unexpected application ID.")
    require(manifest.get(ANDROID + "versionName") == version, "Unexpected APK versionName.")
    require(manifest.get(ANDROID + "versionCode") == code, "Unexpected APK versionCode.")
    require(application.get(ANDROID + "usesCleartextTraffic") == "false", "Cleartext must be disabled.")
    require(ANDROID + "networkSecurityConfig" not in application.attrib, "Review the network security config before publishing.")
    for attr in ("debuggable", "testOnly"):
        require(application.get(ANDROID + attr, "false") == "false", f"Unexpected {attr} application.")
    require(manifest.find("instrumentation") is None, "Unexpected instrumentation.")
    components = {"activity", "activity-alias", "provider", "receiver", "service"}
    actual = sorted(f"{node.tag} {node.get(ANDROID + 'name')}" for node in application
                    if node.tag in components and (node.get(ANDROID + "exported") == "true"
                    or (node.get(ANDROID + "exported") is None and node.find("intent-filter") is not None)))
    expected = sorted(line.strip() for line in (policy / "exported-components.txt").read_text().splitlines() if line.strip())
    require(actual == expected, f"Exported components changed: {actual}")
    permissions = sorted({node.get(ANDROID + "name") for node in manifest if node.tag.startswith("uses-permission")})
    expected = sorted(line.strip().replace("{package}", package) for line in (policy / "permissions.txt").read_text().splitlines() if line.strip())
    require(permissions == expected, f"APK permissions changed: {permissions}")
    activities = application.findall("activity")
    require(len(activities) == 1 and activities[0].get(ANDROID + "name") == LAUNCHER, "Unexpected activities.")


if __name__ == "__main__":
    main()
