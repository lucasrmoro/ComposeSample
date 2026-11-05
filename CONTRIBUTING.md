# Contributing

## Opening an issue

Use the [issue chooser](https://github.com/lucasrmoro/ComposeSample/issues/new/choose)
and select the form that matches your request:

- **Bug report**: unexpected behavior, reproduction steps, expected and actual
  results, the environment, and sanitized evidence.
- **Feature request**: the problem to solve, proposed scope, constraints, and
  observable acceptance criteria.
- **Engineering task**: architecture, testing, CI, security improvements, or
  maintenance, with a concrete scope and validation plan.

Write titles and descriptions in English. Use a short title that describes the
outcome or problem. Search existing issues before opening a new one and link
relevant module paths and related issues. Write `Unknown`, `To be determined`, or
`N/A` when a requested detail cannot be identified or does not apply; explain why
when that affects implementation.

Acceptance criteria should describe observable completion conditions. In
Dependencies, identify actual blockers explicitly, for example: `Blocked by #2
because the testing standard must be defined first.` Write `None` when there are
no known dependencies. Link related issues separately from blockers.

The forms are configured in `.github/ISSUE_TEMPLATE/` and become available in the
website's chooser once they are merged into the default branch, `main`. Blank
issues are disabled in the chooser. Issues submitted through the API or CLI and
later edits are not validated by these forms; preserve the same structure when
using those interfaces.

## Reporting security vulnerabilities

Use [private vulnerability reporting](https://github.com/lucasrmoro/ComposeSample/security/advisories/new)
instead of opening a public issue. Include the affected version or commit, impact,
and safe reproduction details. Do not submit live credentials, signing keys, or
personal data. If you find an exposed credential, the owner must revoke or rotate
it; removing it from a file does not invalidate it.

If private reporting is unavailable, do not disclose the vulnerability in a
public issue. Wait until a private reporting channel is available. Routine security
improvements that do not reveal an exploitable vulnerability can use the
Engineering task form.

## Architecture and validation

Follow the [project README](README.md) for module boundaries, build requirements,
and validation commands. Run the checks relevant to affected modules and record
their actual results in the pull request.

## Development workflow

Follow the [development workflow](README.md#development-workflow) for branch types,
origins, and PR targets. During initial setup, independent work branches originate
from and target `main`; `develop` is reserved for a possible future integration
stage. Use English kebab-case branch
descriptions without issue-number prefixes, and link the issue in the PR body.

Use `task/*` for work that belongs to a feature or bugfix, including its tests or
documentation. Task PRs target their own `feature/*` or `bugfix/*` parent and must
declare it with `Parent branch: <branch-name>` in the description. Independent
test improvements use `chore/*`; there is no `test/*` branch type.

Branch types are separate from semantic commit and PR title types. Use short
English messages in the format `type: description`, without a scope. Semantic
types include `feat`, `fix`, `refactor`, `docs`, `test`, `build`, `ci`, `perf`,
`style`, and `chore`. For breaking changes, add `!` after the type and a
`BREAKING CHANGE:` footer to the commit. Include affected modules, issue links,
validation results, and screenshots for visual changes in PR descriptions.
