# Validate PR title

This check validates the pull request title and requires its type to match the source
branch prefix.

## Requirements

- Use `<branch-type>: <description>` with one space after the colon.
- Use an allowed branch type: `feature`, `bugfix`, `hotfix`, `task`, `docs`, `ci`,
  `refactor`, `build`, or `chore`.
- The description must contain at least one non-whitespace character.
- Do not add a scope or surrounding whitespace.
- The title type must exactly match the source branch prefix.

For example, `docs/update-guide` requires a title such as `docs: update guide`.
`fix: update guide`, `docs(guide): update guide`, and `docs:` fail.

The check validates title format and branch-prefix agreement. It does not judge the
description's wording or validate commit messages. Editing the PR title reruns the
workflow without requiring a new commit.
