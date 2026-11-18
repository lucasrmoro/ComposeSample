"""Validate the pull request branch name and its target branch."""

import json
import os
import re
from pathlib import Path


def main():
    # GitHub writes the event payload to a temporary JSON file for each workflow run.
    event = json.loads(Path(os.environ["GITHUB_EVENT_PATH"]).read_text(encoding="utf-8"))
    pull_request = event["pull_request"]
    repository = os.environ["GITHUB_REPOSITORY"].casefold()
    head = pull_request["head"]["ref"]
    base = pull_request["base"]["ref"]

    # Feature and bugfix branches use two segments; task branches name their parent and task.
    description = r"[a-z][a-z0-9]*(?:-[a-z0-9]+)*"
    pattern = re.compile(
        rf"(feature|bugfix|hotfix|docs|ci|refactor|build|chore)/({description})"
    )
    task_pattern = re.compile(rf"task/({description})/({description})")
    match = pattern.fullmatch(head)
    errors = []

    if pull_request["base"]["repo"]["full_name"].casefold() != repository:
        errors.append("The PR must target a branch in this repository.")

    if head.startswith("task/"):
        task_match = task_pattern.fullmatch(head)
        parent_match = pattern.fullmatch(base)
        if not task_match:
            errors.append(
                "Use task/<parent-name>/<task-name> with exactly three segments. "
                "Both names must use lowercase kebab-case, starting with a letter. "
                "Example: task/stream-page/add-stream-page. Issue-number prefixes are not used."
            )
        if not parent_match or parent_match.group(1) not in ("feature", "bugfix"):
            errors.append("A task PR must target a valid feature/<parent-name> or bugfix/<parent-name> branch.")
        elif task_match and task_match.group(1) != parent_match.group(2):
            errors.append(
                f"Task parent name '{task_match.group(1)}' must match target parent name "
                f"'{parent_match.group(2)}'. Target feature/{task_match.group(1)} "
                f"or bugfix/{task_match.group(1)}."
            )
    elif not match:
        errors.append(
            "Use <type>/<lowercase-kebab-case-description>, starting with a letter. "
            "Allowed types: feature, bugfix, hotfix, task, docs, ci, refactor, build, chore. "
            "Tasks use task/<parent-name>/<task-name>. "
            "Example: ci/add-pull-request-validation. Issue-number prefixes are not used."
        )
    elif base != "main":
        errors.append("Independent work PRs must target main during initial setup.")

    if errors:
        print("❌ Branch flow validation failed:")
        for error in errors:
            print(f"❌ {error}")
        raise SystemExit(1)

    print("✅ Branch name and PR target are valid.")


if __name__ == "__main__":
    main()
