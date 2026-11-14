# Quality gates

These documents describe each required check in the pull request validation workflow,
including what it checks, when it runs, and how to address failures.

Each gate runs through a local composite action in `.github/actions/`. Build, Android
Lint, unit tests, and architecture validation share the `android-setup` action for
JDK, Android SDK, and Gradle setup. The workflow defines triggers, permissions,
concurrency, and jobs.

| Gate | Documentation |
| --- | --- |
| Validate branch name | [validate-branch-name.md](validate-branch-name.md) |
| Validate PR title | [validate-pull-request-title.md](validate-pull-request-title.md) |
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
workflow summary, uploads `quality-gates-report.md` as an artifact, and posts a new
comment on pull requests from branches in this repository. It minimizes older report
comments as outdated, leaving the latest result visible. The table lists each result,
a short correction example, and a link to its guide. Fork pull requests retain the
workflow summary and artifact because their token cannot write comments. The report
job also runs when a gate fails or is skipped, but stops when the workflow is cancelled.
