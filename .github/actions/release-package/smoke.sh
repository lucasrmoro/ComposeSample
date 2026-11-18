#!/usr/bin/env bash
set -euo pipefail

package=br.com.composesample
if [[ "$FLAVOR" == dev ]]; then
    package+=.dev
fi

if [[ -f previous/ComposeSample.apk ]]; then
    adb install previous/ComposeSample.apk
fi
adb install -r signed/ComposeSample.apk
result="$(adb shell am start -W -n "$package/br.com.composesample.activity.LauncherActivity")"
grep -q 'Status: ok' <<< "$result"
sleep 5
adb shell pidof "$package" > /dev/null
if adb logcat -b crash -d | grep -F "Process: $package,"; then
    exit 1
fi
