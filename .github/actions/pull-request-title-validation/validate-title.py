"""Validate a pull request title and its match with the source branch type."""

import json
import os
import re
from pathlib import Path


def main():
    # Read the title and source branch from GitHub's event payload instead of shell arguments.
    event = json.loads(Path(os.environ["GITHUB_EVENT_PATH"]).read_text(encoding="utf-8"))
    pull_request = event["pull_request"]
    title = pull_request["title"]
    branch_type = pull_request["head"]["ref"].split("/", 1)[0]

    # The title prefix follows the branch type; the description is required and has no scope.
    match = re.fullmatch(
        r"(feature|bugfix|hotfix|task|docs|ci|refactor|build|chore): (\S[^\r\n]*)",
        title,
    )
    errors = []

    if not match or title != title.strip():
        errors.append(
            "Use <branch-type>: <description>, without a scope or surrounding whitespace. "
            "Provide a non-empty description. Example: ci/validate-pull-request-titles "
            "requires a title such as ci: validate PR titles."
        )
    elif match.group(1) != branch_type:
        errors.append("The title type must exactly match the source branch prefix.")

    if errors:
        print("❌ PR title validation failed:")
        for error in errors:
            print(f"❌ {error}")
        raise SystemExit(1)

    print("✅ PR title format and source branch prefix match.")


if __name__ == "__main__":
    main()
