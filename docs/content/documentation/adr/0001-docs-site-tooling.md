# ADR-0001: Docs site built with MkDocs Material

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

We need to publish project documentation, planning docs, research, brainstorming, and design notes as a browsable site via GitHub Pages, kept separate from application code. The tool needs to support a multi-section site (Documentation / Planning / Brainstorm / Design) with reasonable navigation and search out of the box, without requiring a new language/toolchain skill beyond what the rest of the stack already needs.

## Options considered

- **Plain Jekyll** — native to GitHub Pages, zero extra build tooling. But the default theme/nav is basic; getting a Material-like multi-section nav and search requires about as much config effort as just writing an `mkdocs.yml` directly, so the "least effort" appeal doesn't hold up under a closer look.
- **Docusaurus** — powerful (MDX, versioning, React components), but Node/React-based. Heavier than needed for a documentation site with no interactive/versioned-docs requirement, and a mismatch with the "no separate skill just to maintain docs" goal.
- **MkDocs Material** — pure Markdown content, Python-only build, navigation tabs/sections/search built in, minimal config for what we need today.

Since all three consume plain Markdown, the content itself is portable between them — the real switching cost of picking wrong is limited to rewriting nav/config, not losing work.

## Decision

Use MkDocs with the Material theme. Config lives at `docs/mkdocs.yml`, content under `docs/content/`, built/served via `docker compose up docs` (see [ADR-0002](0002-containerized-dev-tooling.md)).

`docs/requirements.txt` pins both `mkdocs<2` and `mkdocs-material<10`.

## Consequences

- Docs site is Python tooling, consistent with the rest of the environment's venv/container conventions.
- New top-level sections require a manual `nav:` entry in `mkdocs.yml` (documented in `CLAUDE.md`).
- Content stays plain Markdown, so a future migration to Docusaurus or elsewhere would not require rewriting the actual documentation.

## Revisit when

The MkDocs Material maintainers have publicly warned that **MkDocs 2.0** will remove the plugin system, rewrite theming with no migration path, and (as of their announcement) ship without a settled license for production use. Revisit this decision only when MkDocs 2.0 actually ships **with** a real migration path and clear licensing — not before. Also revisit if the docs site ever needs genuine interactivity/MDX that MkDocs structurally can't provide.
