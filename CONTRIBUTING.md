# Contributing to Chetna

## Branching model

- **`main`** — production/stable. Protected. Only updated via a PR from `dev`.
- **`dev`** — integration branch. Protected. Only updated via PRs from feature branches.
- Individual work branches from `dev`, prefixed by kind, kebab-case description after the slash:

  | Prefix | For | Example |
  |---|---|---|
  | `feature/` | New functionality | `feature/toddler-kids-mode`, `feature/consent-verification-flow` |
  | `fix/` | Bug fixes | `fix/testcontainers-docker-detection`, `fix/docs-workflow-env-vars` |
  | `chore/` | Maintenance, deps, tooling — no behavior change | `chore/upgrade-spring-boot-4`, `chore/bump-github-actions` |
  | `docs/` | Documentation-only changes | `docs/add-adr-0012`, `docs/update-contributing-guide` |

## Workflow

1. Branch from `dev`: `git checkout dev && git pull && git checkout -b feature/my-thing`
2. Do the work, commit, push.
3. Open a PR into `dev`. Must pass all required checks, get at least one approval (including a [Code Owner](.github/CODEOWNERS)), and have all conversations resolved.
4. Once merged, the feature branch is auto-deleted.
5. Periodically, once `dev` is in a releasable state, a maintainer opens a PR from `dev` into `main`. Same requirements apply.
6. **After merging `dev` → `main`, resync `dev`** so it doesn't drift out of sync:
   ```bash
   git checkout dev
   git fetch origin
   git merge origin/main -m "Merge main into dev to resync after squash/rebase merge"
   git push origin dev
   ```
   This step is necessary, not optional: squash/rebase merges create new commits on `main` with different hashes than `dev`'s originals, even though the content is identical. Without resyncing, `dev` and `main` show as diverged ("N ahead, N behind") indefinitely. Use a real merge here, not `git reset --hard` + force-push — branch protection blocks force-pushes to `dev` (correctly), and a forward merge resolves the divergence without needing to bypass that. `dev` will show as ahead of `main` afterward (it now contains a merge commit `main` doesn't) — that's expected, not a problem; only "behind" indicates missing content.

## Merge strategy

**Squash and merge only.** Every PR becomes a single, clean commit on the target branch — merge commits and unsquashed rebases are disabled at the repository level. Write a clear, descriptive PR title; it becomes the squashed commit's message.

## Branch protection (enforced on both `main` and `dev`)

- Pull requests required — no direct pushes.
- At least one approval, including a Code Owner.
- All required status checks (CI workflows) must pass.
- All PR conversations must be resolved.
- The branch must be up to date with its target before merging.

## Documentation discipline

See [`CLAUDE.md`](CLAUDE.md) for where different kinds of content (ADRs, planning docs, research, brainstorming, design) belong in this repo — the same discipline applies whether you're a contributor or an AI assistant working on this codebase.
