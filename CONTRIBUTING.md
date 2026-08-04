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
5. Periodically, once `dev` is in a releasable state, a maintainer opens a PR from `dev` into `main`, merged with **"Create a merge commit"** (not squash, not rebase — see [Merge strategy](#merge-strategy)). Because `dev` is already squash-clean by this point, this doesn't add commit noise to `main` — it copies `dev`'s existing commits over unchanged and adds one merge commit. No new hashes, so `dev` and `main` share identical history immediately afterward — **no resync step needed.**

   If a `dev` → `main` PR is ever merged via squash or rebase instead (by mistake, or because the setting drifted), `dev` will show as diverged from `main` ("N ahead, N behind"). Fix it the same way, without force-pushing (branch protection blocks that on `dev`, correctly):
   ```bash
   git checkout dev
   git fetch origin
   git merge origin/main -m "Merge main into dev to resync after squash/rebase merge"
   git push origin dev
   ```
   `dev` will show as ahead of `main` afterward (it now has a merge commit `main` doesn't) — that's expected, not a problem; only "behind" indicates missing content.

## Merge strategy

- **`feature/`/`fix/`/`chore/`/`docs/` → `dev`: squash and merge.** Every such PR becomes a single, clean commit on `dev` — keeps `dev`'s history one-commit-per-change. Write a clear, descriptive PR title; it becomes the squashed commit's message.
- **`dev` → `main`: create a merge commit** (GitHub's "Create a merge commit" option, not squash or rebase). `dev`'s history is already squash-clean, so this doesn't reintroduce noise into `main` — it just copies `dev`'s existing commits over as-is (same hashes) plus one merge commit, which is what keeps `dev` and `main` from diverging.
- Repo settings (`Settings → General → Pull Requests`) must allow both "Allow squash merging" and "Allow merge commits" — squash for feature branches, merge commit for the `dev` → `main` promotion specifically. "Allow rebase merging" should stay off; a rebase-merged `dev` → `main` PR causes the same divergence a squash does.

## Branch protection (enforced on both `main` and `dev`)

- Pull requests required — no direct pushes.
- At least one approval, including a Code Owner.
- All required status checks (CI workflows) must pass.
- All PR conversations must be resolved.
- The branch must be up to date with its target before merging.

## Documentation discipline

See [`CLAUDE.md`](CLAUDE.md) for where different kinds of content (ADRs, planning docs, research, brainstorming, design) belong in this repo — the same discipline applies whether you're a contributor or an AI assistant working on this codebase.
