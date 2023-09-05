Branching strategy
==================

This document briefly explains the (git) branching strategy adopted
for this project.

## General rules

- **`master`** contains the most up-to-date code and is kept
  stables/shielded via reviewed pull requests and proper DevOps triggers and checks.

- **features** and **bug/hotfixes** go on separate branches which are then merged onto
  `master` (or a release branch if it is a release-specific change) via pull requests.

- **releases** go on separate branches and are kept separate from `master`.

## Naming convention

Definition by examples:

- `release/<version_number>`
- `feature/<feature-name>`
- `feature/<feature-area>/<feature-name>`
- `bugfix/<description>`
- `hotfix/<description>`
- `users/<username>/backup`
- `users/<username>/<description>`

NOTE: use all lower-case and dash `-` as a separator when a description/name
requires multiple words, e.g. "*feature/awesomeness/this-is-the-killer*".

NOTE: We mean by *hotfixes* those fixes on bugs that affect "live" releases, while
*bugfixes* those who affect still unreleased code.

## New feature/fix

When contributing to implement a new feature or to fix an issue,
always branch off the `master` branch (or a release branch if that
is a release-specific hotfix)

```sh
$ git checkout -b bugfix/miserable-little-bug
... [fix the bug]
$ git commit -m "I fixed ittT."
$ git push --set-upstream <github-remote> bugfix/miserable-little-bug
```

Now that the branch is on GitHub, you can create a [Pull Request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request)
on GitHub to start the review process and eventually apply the changes to the codebase.

## Releases

When a new release is about to be published, a new dedicated branch is created
with the name `release/<X.Y.Z>`, then tags can be used to temporarily mark
different steps of the release (beta, or a release candidate, etc.).

When finalizing a release, all interim tags (except maybe the final release)
should be finally cleaned up. The release branch shall **not** be merged back onto
`master` as [other branching strategies](https://gitversion.net/docs/learn/branching-strategies/gitflow/examples)
suggest: we prefer to avoid relying on tags but rather on branches to
keep track of the different releases.

When moved out of support, release branches should be
[locked](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches#lock-branch)
to avoid further development on it.

Release version names (try to) follow the **MAJOR.MINOR.PATCH** scheme as proposed by the
[SEMVER](https://semver.org/) specification, that is:

1. MAJOR version when you make incompatible API changes
1. MINOR version when you add functionality in a backward compatible manner
1. PATCH version when you make backward compatible bug fixes


### Syncing changes between `master` and release(s)

When a change needs to be applied to both master and 1+ release branches, there are usually
2 options: a) merge the related PR to `master` then port the change to the release branch
or b) viceversa.

Our choice is to always apply to the main `master` branch, then port/cherry-pick the changes
onto the release(s).

Changes that are specific to a release then do not need to be applied elsewhere.


## References

- [**DEV** | *Branching & Merging Strategies – Release Flow*](https://dev.to/jeastham1993/branching-merging-strategies-release-flow-18f3)
- [**azure-devops-docs** | *Adopt a Git branching strategy*](https://github.com/MicrosoftDocs/azure-devops-docs/blob/main/docs/repos/git/git-branching-guidance.md)
