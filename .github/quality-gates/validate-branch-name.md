# Validate branch name

This check validates the source branch name and the pull request's target repository
and branch. It runs when a pull request is opened, reopened, updated, edited, or marked
ready for review.

## Requirements

- Use `<type>/<lowercase-kebab-case-description>` for non-task branches and
  `task/<parent-name>/<task-name>` for task branches, with exactly three segments.
- Each description, parent name, and task name starts with a letter and contains
  lowercase letters, digits, and single hyphens between words.
- Allowed types: `feature`, `bugfix`, `hotfix`, `task`, `docs`, `ci`, `refactor`,
  `build`, and `chore`.
- Do not prefix the branch name with an issue number.
- The pull request must target a branch in this repository.
- During initial setup, independent work branches target `main`.
- A task branch must target `feature/<parent-name>` or `bugfix/<parent-name>`.
  Its parent-name segment must exactly match the target branch's description.

## Examples

- Valid: `feature/add-login`, `ci/validate-pull-request-titles`
- Invalid: `feature/65-add-login`, `Feature/add-login`, `feature/add_login`

| Task branch | PR target | Result |
| --- | --- | --- |
| `task/stream-page/add-stream-page` | `feature/stream-page` | Valid |
| `task/stream-page/fix-playback` | `bugfix/stream-page` | Valid |
| `task/stream-page/add-stream-page` | `feature/login` | Invalid: parent name mismatch |
| `task/stream-page/add-stream-page` | `main` | Invalid: target must be a matching feature or bugfix |
| `task/stream-page/add-stream-page` | `hotfix/stream-page` | Invalid: task targets cannot be hotfixes |
| `task/add-stream-page` | `feature/stream-page` | Invalid: missing task-name segment |
| `task/stream-page/add/extra` | `feature/stream-page` | Invalid: extra segment |
| `task//add-stream-page` | `feature/stream-page` | Invalid: empty parent name |
| `task/Stream-page/add-stream-page` | `feature/stream-page` | Invalid: uppercase parent name |
| `task/stream-page/add_stream_page` | `feature/stream-page` | Invalid: underscore in task name |
| `task/76-stream-page/add-stream-page` | `feature/stream-page` | Invalid: issue-number prefix in parent name |
| `task/stream-page/76-add-stream-page` | `feature/stream-page` | Invalid: issue-number prefix in task name |

Validation errors distinguish malformed task names, invalid target branches, and
parent-name mismatches. Failed validation exits with a nonzero status.

The check validates the PR's current target. It does not verify how or when the branch
was created, and it does not prevent creating a branch. Fork PRs may require maintainer
approval under the repository's GitHub Actions settings.
