#!/usr/bin/env bash
set -euo pipefail

# GitHub provides the event/ref flags; the version job provides APPROVED_REVISION.
# Check them before invoking Gradle so an unmerged or mismatched checkout cannot produce an APK.
if [[ "$GITHUB_REF" != refs/heads/main || "$GITHUB_REF_PROTECTED" != true ||
      ( "$GITHUB_EVENT_NAME" != push && "$GITHUB_EVENT_NAME" != workflow_dispatch ) ||
      ! "$APPROVED_REVISION" =~ ^[0-9a-f]{40}$ || "$(git rev-parse HEAD)" != "$APPROVED_REVISION" ]]; then
    echo "Release APKs require the reviewed revision already merged into protected main."
    exit 1
fi
