#!/usr/bin/env bash
set -euo pipefail

# Verify the signed artifact bytes before trusting metadata from the attestation.
(cd signed && sha256sum --check SHA256SUMS)

# Require the official signer workflow and our custom predicate, then inspect its source and release fields.
attestation="$(gh attestation verify "signed/$APK_NAME" --repo "$GITHUB_REPOSITORY" \
    --signer-workflow "$GITHUB_REPOSITORY/.github/workflows/android-release.yml" \
    --predicate-type https://github.com/lucasrmoro/ComposeSample/attestations/android-release/v1 \
    --format json)"
jq -e --arg source "$REVISION" --arg version "$VERSION" --argjson code "$VERSION_CODE" \
    --arg repository "$GITHUB_REPOSITORY" --arg apk "$APK_NAME" '
    any(.[];
        .verificationResult.statement.predicate.sourceCommit == $source and
        .verificationResult.statement.predicate.sourceRepository == $repository and
        .verificationResult.statement.predicate.release.apkName == $apk and
        .verificationResult.statement.predicate.release.flavor == "prd" and
        .verificationResult.statement.predicate.release.versionName == $version and
        .verificationResult.statement.predicate.release.versionCode == $code
    )
' <<< "$attestation" > /dev/null
