"""Create a Markdown report from pull request quality gate job results."""

import json
import os
from pathlib import Path


GATES = (
    (
        "branch-validation",
        "Validate branch name",
        "validate-branch-name.md",
        "Rename `feature/123-add-login` to `feature/add-login` and target `main`.",
    ),
    (
        "pull-request-title-validation",
        "Validate PR title",
        "validate-pull-request-title.md",
        "For `feature/add-login`, use `feature: add login`.",
    ),
    (
        "release-version",
        "Validate release version",
        "android-release.md",
        "Update from main and ensure the PR title describes the intended release increment.",
    ),
    (
        "build",
        "Build",
        "build.md",
        "Run `./gradlew :app:assembleDevDebug` and fix the reported compiler or resource error.",
    ),
    (
        "lint",
        "Android Lint",
        "android-lint.md",
        "Run `./gradlew :app:lintDevDebug` and fix the reported file and line.",
    ),
    (
        "unit-tests",
        "Unit tests",
        "unit-tests.md",
        "Run `./gradlew :app:testDevDebugUnitTest` and fix the failing assertion.",
    ),
    (
        "architecture-validation",
        "Architecture validation",
        "architecture-validation.md",
        "Run `./gradlew architectureCheck`; replace a feature's data dependency with its domain API.",
    ),
)

# Keep labels aligned with the job result names sent by the workflow's quality-report job.
RESULT_LABELS = {
    "success": "✅ Passed",
    "failure": "❌ Failed",
    "skipped": "⚪ Skipped",
    "cancelled": "⚪ Cancelled",
}


def main():
    # The workflow passes each prerequisite job result as JSON; absent jobs are shown as skipped.
    results = json.loads(os.environ["GATE_RESULTS_JSON"])
    # The event payload identifies the PR source commit used for links to gate documentation.
    event = json.loads(Path(os.environ["GITHUB_EVENT_PATH"]).read_text(encoding="utf-8"))
    head = event["pull_request"]["head"]
    source_url = f"{os.environ['GITHUB_SERVER_URL']}/{head['repo']['full_name']}/blob/{head['sha']}"
    run_url = (
        f"{os.environ['GITHUB_SERVER_URL']}/{os.environ['GITHUB_REPOSITORY']}"
        f"/actions/runs/{os.environ['GITHUB_RUN_ID']}"
    )

    lines = [
        "# Pull request quality gates",
        "",
        "Results for this workflow run. Open a failed gate in the "
        f"[workflow logs]({run_url}) for its exact error.",
        "",
        "| Gate | Result | Example correction | Guide |",
        "| --- | --- | --- | --- |",
    ]
    for job_id, title, guide, example in GATES:
        result = results.get(job_id, {}).get("result", "skipped")
        label = RESULT_LABELS.get(result, f"⚪ {result.title()}")
        guide_url = f"{source_url}/.github/quality-gates/{guide}"
        lines.append(f"| {title} | {label} | {example} | [Guide]({guide_url}) |")

    lines.extend(
        (
            "",
            "A skipped gate did not run, often because a prerequisite gate failed. "
            "Fix that gate first and rerun the checks.",
            "",
        )
    )
    report = "\n".join(lines)
    # Save the same report as an artifact and in GitHub's run summary for easy review.
    Path("quality-gates-report.md").write_text(report, encoding="utf-8")
    with Path(os.environ["GITHUB_STEP_SUMMARY"]).open("a", encoding="utf-8") as summary:
        summary.write(report)


if __name__ == "__main__":
    main()
