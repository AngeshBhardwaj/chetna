# Instructions for Claude

## Documentation discipline — read this first

This project treats the repo, not chat history, as the source of truth. **Any decision made in conversation that affects architecture, tooling, process, or scope must be written to the appropriate file under `docs/` before the task is considered done.** If a decision only exists in the conversation, treat it as not yet decided — do not rely on memory of past sessions to preserve it.

Where things go:

| Kind of content | Location |
|---|---|
| Architecturally-significant decision (tool choice, pattern, trade-off, "why X not Y") | `docs/content/documentation/adr/`, new `NNNN-title.md` from `template.md`, plus an entry in `adr/index.md`'s table |
| Technical/code documentation (architecture overviews, module guides, API references) | `docs/content/documentation/` |
| Roadmap, feature specs | `docs/content/planning/` |
| Research (competitive analysis, feasibility, compliance) | `docs/content/planning/research/` |
| Undecided ideas, raw notes, open questions | `docs/content/brainstorm/` |
| UX/UI, wireframes, diagrams | `docs/content/design/` |
| Application code | `apps/` |

When adding a new top-level page or section under `docs/content/`, also add it to the `nav:` block in `docs/mkdocs.yml` — a page that isn't in `nav` won't be reachable from the published site.

When in doubt whether something rises to the level of an ADR: write one. It's cheap to write and expensive to have silently lost. Every ADR must include a concrete "Revisit when" trigger (a specific event/metric/milestone, not "if it becomes a problem").

## Environment conventions

- Never install Python packages into the host's system/user Python — always use a venv, including inside containers (see `docs/Dockerfile` for the pattern).
- Local dev tooling (the docs site, and future services) runs via `docker compose`, not ad hoc host installs.
- This repo's git identity is the personal GitHub account (`Angesh` / `angeshbhardwaj@outlook.com`, origin over SSH via the `github-personal` key) — do not assume work-account context.
