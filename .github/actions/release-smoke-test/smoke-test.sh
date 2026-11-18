#!/usr/bin/env bash
set -euo pipefail

# For production upgrades, install the prior APK first so Android updates the existing app and keeps its data.
if [[ -f previous.apk ]]; then adb install previous.apk; fi

# Replace the installed package with the signed candidate and start its launcher activity.
adb install -r "signed/$APK_NAME"
set -o pipefail
adb shell am start -W -n "$APK_PACKAGE/br.com.composesample.activity.LauncherActivity" | grep 'Status: ok'

# Allow startup work to run, then confirm the process is still alive and no fatal crash was recorded.
sleep 5
adb shell pidof "$APK_PACKAGE"
crash_log="$(adb logcat -b crash -d)"
! grep -F "Process: $APK_PACKAGE," <<< "$crash_log"
