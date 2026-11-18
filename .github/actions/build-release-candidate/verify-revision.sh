#!/usr/bin/env bash
set -euo pipefail

# Release builds only run for an approved SHA checked out from protected main.
if [[ "$GITHUB_REF" != refs/heads/main || "$GITHUB_REF_PROTECTED" != true ||
      ( "$GITHUB_EVENT_NAME" != push && "$GITHUB_EVENT_NAME" != workflow_dispatch ) ||
      ! "$APPROVED_REVISION" =~ ^[0-9a-f]{40}$ || "$(git rev-parse HEAD)" != "$APPROVED_REVISION" ]]; then
    echo "Release APKs require the reviewed revision already merged into protected main."
    exit 1
fi
