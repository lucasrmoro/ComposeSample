# Quality gates

These documents describe each required check in the pull request validation workflow,
including what it checks, when it runs, and how to address failures.

Each gate runs through a local composite action in `.github/actions/`. Build, Android
Lint, unit tests, and architecture validation share the `android-setup` action for
JDK, Android SDK, and Gradle setup. The workflow defines triggers, permissions,
concurrency, and jobs.

Gradle build cache and parallel module execution are enabled in `gradle.properties`.
The shared setup action is the only Gradle User Home cache manager. Same-repository
pull requests can save cache entries in their own merge-ref scope; fork pull requests
only read existing entries. Cache keys distinguish jobs, so parallel runners save
separate entries. Gradle validates task inputs before reusing cached outputs.

The `Build Android Release` workflow runs Gradle on the default `main` branch for
both release flavors, including assembly, lint, unit tests, and architecture
validation. The shared setup action saves the Gradle User Home cache at the end
of each build job, so those runs also warm the cache for later pull requests.
There is no separate cache warmup workflow or extra post-merge Gradle run.

| Gate | Documentation |
| --- | --- |
| Validate branch name | [validate-branch-name.md](validate-branch-name.md) |
| Validate PR title | [validate-pull-request-title.md](validate-pull-request-title.md) |
| Validate release version | [android-release.md](android-release.md) |
| Build | [build.md](build.md) |
| Android Lint | [android-lint.md](android-lint.md) |
| Unit tests | [unit-tests.md](unit-tests.md) |
| Architecture validation | [architecture-validation.md](architecture-validation.md) |

The workflow runs for pull requests when they are opened, reopened, updated with new
commits, edited, or marked ready for review. Runs for the same pull request cancel an
older run when a newer run starts. The repository ruleset uses these check results for
merge eligibility. CodeRabbit review is advisory and is not a required status check.

Branch and title validation run first and independently. Build, Android Lint, unit
tests, and architecture validation start in parallel only after both metadata checks
pass. If either check fails, those Android jobs are skipped and the report shows why.

After the gates finish, the Quality gate report job shows a Markdown table in the
workflow summary. Android Lint errors are also translated from the Gradle XML
report into GitHub check annotations with exact file and line locations. The report
job runs when a gate fails or is skipped, but stops when the workflow is cancelled.
It uses a read-only token and does not post PR comments, so code from a pull request
cannot use a write-enabled token through this job.
