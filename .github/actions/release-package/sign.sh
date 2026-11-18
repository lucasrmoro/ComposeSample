#!/usr/bin/env bash
set -euo pipefail
umask 077

: "${RELEASE_KEYSTORE_BASE64:?Configure the production keystore in the signing environment}"
: "${RELEASE_KEY_ALIAS:?Configure the signing alias}"
: "${RELEASE_STORE_PASSWORD:?Configure the keystore password}"
: "${RELEASE_KEY_PASSWORD:?Configure the key password}"
: "${RELEASE_CERT_SHA256:?Configure the expected SHA-256 signing certificate fingerprint}"

temporary="$(mktemp -d)"
trap 'rm -rf -- "$temporary"' EXIT
sdk_tools="$ANDROID_HOME/build-tools/36.0.0"
printf '%s' "$RELEASE_KEYSTORE_BASE64" | base64 -d > "$temporary/release.keystore"
unset RELEASE_KEYSTORE_BASE64
mkdir -p signed
"$sdk_tools/zipalign" -P 16 -f 4 candidate/unsigned.apk "$temporary/aligned.apk"
"$sdk_tools/apksigner" sign --ks "$temporary/release.keystore" --ks-key-alias "$RELEASE_KEY_ALIAS" \
    --ks-pass env:RELEASE_STORE_PASSWORD --key-pass env:RELEASE_KEY_PASSWORD \
    --out signed/ComposeSample.apk "$temporary/aligned.apk"
unset RELEASE_STORE_PASSWORD RELEASE_KEY_PASSWORD
rm -f signed/ComposeSample.apk.idsig
"$sdk_tools/zipalign" -c -P 16 4 signed/ComposeSample.apk
"$sdk_tools/apksigner" verify --verbose --print-certs signed/ComposeSample.apk > "$temporary/certificate"
actual="$(sed -n 's/^Signer #1 certificate SHA-256 digest: //p' "$temporary/certificate")"
expected="$(printf '%s' "$RELEASE_CERT_SHA256" | tr -d ':' | tr '[:upper:]' '[:lower:]')"
[[ "$expected" =~ ^[0-9a-f]{64}$ && "$actual" == "$expected" ]]
[[ "$(grep -c '^Signer #[0-9]* certificate SHA-256 digest:' "$temporary/certificate")" == 1 ]]
printf '%s\n' "$actual" > signed/signing-certificate-sha256.txt
cp candidate/release.json candidate/RELEASE_NOTES.md candidate/sbom.cdx.json signed/
(cd signed && sha256sum ComposeSample.apk release.json RELEASE_NOTES.md sbom.cdx.json signing-certificate-sha256.txt > SHA256SUMS)
