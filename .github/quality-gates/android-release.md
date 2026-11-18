# Android release policy

Every reviewed update to `main` carries a version decision, including documentation
and configuration changes. Update the two fields in `release.properties` and replace
`RELEASE_NOTES.md` with notes headed `# Version X.Y.Z`. The same version becomes
Android's version name, the `vX.Y.Z` tag, and the GitHub Release title. Android's
`versionCode` must increase and cannot exceed 2,100,000,000.

| Change | Example from 1.4.2 (code 42) |
| --- | --- |
| Compatible fix, documentation or configuration | 1.4.3 (43) |
| New backwards-compatible feature | 1.5.0 (43) |
| Breaking change | 2.0.0 (43), with impact and migration in the notes |
| Production hotfix | Branch `hotfix/fix-startup` from the latest supported main release; target current main with a matching PR title and use the next unused version/code. |

Use `main` directly for reviewed work. If an integration branch called `develop`
is explicitly reinstated, apply that branch's review gates, promote through a
reviewed PR to `main` with a fresh release version, and back-merge production
hotfixes into `develop` through a reviewed PR. Use the existing source-branch
and title conventions. An overlapping PR must update from `main` and pick a
version and code above its latest accepted revision. Version bumps happen in
reviewed PRs. Do not create independent post-merge version commits.

Support the most recent production release. For rollback, revert the relevant
source via a reviewed PR and choose a new version and higher code. Keep the
original signing key and preserve the old immutable release.

## Repository setup before the first merge

- Enable immutable releases in the repository settings. Protect `main` from
  unreviewed updates and require up-to-date status checks, including
  **Validate release version**, before merging. Configure the required check
  after it first appears on this PR.
- Create `android-signing-prd` and `android-signing-dev` environments restricted
  to `main`. Use separate stable signing keys. In each environment set secrets
  `RELEASE_KEYSTORE_BASE64`, `RELEASE_STORE_PASSWORD` and
  `RELEASE_KEY_PASSWORD`. Set variables `RELEASE_KEY_ALIAS` and
  `RELEASE_CERT_SHA256` from the signing certificate. Restrict deployment
  approval and credentials to the designated maintainers.
- Create a main-only `android-publication` environment. Set
  `RELEASE_PUBLICATION_ENABLED=true` there after enabling immutable releases
  and configuring both signing environments.
- Keep encrypted backups of each keystore and its recovery information in a
  restricted vault. Base64 is transport encoding. Record the expected signer
  separately and periodically test recovery. Store any crash retracing mapping
  in a restricted system keyed by variant and source SHA. The workflow never
  uploads plaintext R8 mappings.
- The signed dev APK appears as the `signed-dev` workflow artifact. Users with
  repository access can download it. Use the dev package
  `br.com.composesample.dev` alongside the production package
  `br.com.composesample`.

## Publishing and recovery

The workflow accepts an exact reviewed first-parent commit from `main`.
It runs release assembly, lint, local tests, architecture checks, a redacted
Gitleaks scan of the release change and packaged artifact, and an OSV audit of
resolved Maven runtime components. Hosted runners sign each flavor separately
and verify its expected certificate. The smoke jobs install and launch both
APKs on an emulator and test upgrading production from the prior published APK
when one exists. Production publishes only after both smoke jobs pass.

A failed revision stays pending for recovery. Use **Re-run failed jobs** to
reuse the original signed artifacts, or dispatch **Android release** from
`main` with the exact SHA after resolving an external issue. For example,
if `v1.5.0` failed during a draft upload, rerun its failed publication job.
It compares existing bytes and uploads only missing assets. It never moves an
existing tag or overwrites an asset. A newer revision cannot publish until its
predecessor has an immutable release. Recover pending or overflowed runs
oldest first, even if the Actions queue started them out of order.

A broken source revision that can never pass its gates is a release incident.
A code fix requires a new reviewed revision. Any exception to the per-update
publication policy requires an explicit owner decision and a separate policy
change. Never publish a failed APK to unblock the queue.

## Install and verify

Download the production assets and verify the checksums and public signer.
Compare the certificate against the independently recorded fingerprint.

```sh
sha256sum -c SHA256SUMS
apksigner verify --verbose --print-certs ComposeSample.apk
gh attestation verify ComposeSample.apk --repo lucasrmoro/ComposeSample
adb install -r ComposeSample.apk
```

Both variants use R8 shrinking, optimization and obfuscation. The APK checks
verify package/version, manifest restrictions, and intended exported
components and permissions. Keep backend authorization and confidential
credentials server-side. The release build deliberately omits the local
TMDB token, so catalog features that rely on that token require a server-side
integration before distributing a fully functional catalog. Exercise those
paths with a controlled backend before claiming release acceptance. Signing
verifies integrity and signer identity. Obfuscation raises the effort of
reverse engineering. Runtime tampering and vulnerabilities remain possible.
