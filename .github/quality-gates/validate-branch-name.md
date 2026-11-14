# Validate branch name

This check validates the source branch name and the pull request's target repository
and branch. It runs when a pull request is opened, reopened, updated, edited, or marked
ready for review.

## Requirements

- Use `<type>/<lowercase-kebab-case-description>`.
- The description starts with a letter and contains lowercase letters, digits, and
  single hyphens between words.
- Allowed types: `feature`, `bugfix`, `hotfix`, `task`, `docs`, `ci`, `refactor`,
  `build`, and `chore`.
- Do not prefix the branch name with an issue number.
- The pull request must target a branch in this repository.
- During initial setup, independent work branches target `main`.
- A `task/*` branch targets a `feature/*` or `bugfix/*` parent branch.

## Examples

- Valid: `feature/add-login`, `ci/validate-pull-request-titles`
- Invalid: `feature/65-add-login`, `Feature/add-login`, `feature/add_login`

The check validates the PR's current target. It does not verify how or when the branch
was created, and it does not prevent creating a branch. Fork PRs may require maintainer
approval under the repository's GitHub Actions settings.
