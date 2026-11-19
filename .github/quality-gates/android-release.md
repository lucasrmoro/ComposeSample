# Android release policy

The pipeline derives the version from the verified PR title and the immutable
squash commit subject on `main`. Use the validated PR title as the squash subject.
A GitHub-added pull request number is permitted. Keep the source branch type and
the title prefix aligned. A release preview displays the calculated version on
each PR. Release notes use the reviewed subject from the merged commit.

| PR title | Version from 1.4.2 (code 42) |
| --- | --- |
| `bugfix: fix startup` | 1.4.3 (43) |
| `feature: add offline browsing` | 1.5.0 (43) |
| `feature: [breaking] change login contract` | 2.0.0 (43) |
| `hotfix: fix startup` | 1.4.3 (43) |
| `docs:`, `ci:`, `refactor:`, `build:`, or `chore:` | 1.4.3 (43) |

Use `[breaking]` immediately after the title prefix for incompatible changes.
Describe impact and migration in the PR. Use a patch when a reviewed change does
not add a compatible feature or break compatibility.

The first accepted revision after the legacy baseline starts at version 1.0.0,
regardless of its PR title. Later revisions bump SemVer from the validated PR
title. The legacy APK used Android code 1; every accepted revision increments
the code by one, including docs and CI changes. Retrying the same SHA yields the
same version.
The pipeline passes the calculated version to Gradle and uses it for the tag and
release title. The default for local builds remains the legacy version and code.
The APK attestation records both the selected source commit and workflow revision,
so a retry of an older commit remains traceable to the code that built its APK.

Use `main` directly for reviewed work. If an integration branch called `develop`
is explicitly reinstated, apply that branch's review gates, promote through a
reviewed PR to `main` with a fresh release version, and back-merge production
hotfixes into `develop` through a reviewed PR. Use the existing source-branch
and title conventions. An overlapping PR updates from `main` so the preview
uses the latest accepted revision. The automation never commits a version bump.

Support the most recent production release. For rollback, revert the relevant
source via a reviewed PR. The pipeline gives the rollback a higher code. Keep the
original signing key and preserve the old immutable release.

## Repository setup before the first merge

- Configure squash merges to default to the pull request title. Enable immutable releases in the repository settings. Protect `main` from
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

Production APK generation starts only after a PR is merged into protected `main`.
The workflow checks that the requested SHA is the merged commit of a PR targeting
`main`, is on its first-parent history, and is checked out before Gradle starts.
The PR workflow only previews the release version and runs the existing gates.
It runs release assembly, lint, local tests, architecture checks, a redacted
Gitleaks scan of the release change and packaged artifact, and an OSV audit of
resolved Maven runtime components. `build-release-candidate` orchestrates these checks;
APK validation, secret scanning, and dependency auditing live in separate local
actions, with their implementation scripts beside each action definition.
Hosted runners sign each flavor separately and verify its expected certificate.
The smoke jobs install and launch both
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
The APK name includes the flavor, build type, and release tag. For example,
the production APK for `v1.1.0` is `ComposeSample-prd-release-v1.1.0.apk`.

```sh
apk=ComposeSample-prd-release-v1.1.0.apk
sha256sum -c SHA256SUMS
apksigner verify --verbose --print-certs "$apk"
gh attestation verify "$apk" --repo lucasrmoro/ComposeSample \
  --predicate-type https://github.com/lucasrmoro/ComposeSample/attestations/android-release/v1
adb install -r "$apk"
```

Both variants use R8 shrinking, optimization and obfuscation. The APK checks
verify package and version, require a single exported launcher, and reject
debuggable APKs, cleartext traffic, and test components. Android Lint checks
manifest security as part of each release build. Keep backend authorization and confidential
credentials server-side. The release build deliberately omits the local
TMDB token, so catalog features that rely on that token require a server-side
integration before distributing a fully functional catalog. Exercise those
paths with a controlled backend before claiming release acceptance. Signing
verifies integrity and signer identity. Obfuscation raises the effort of
reverse engineering. Runtime tampering and vulnerabilities remain possible.
