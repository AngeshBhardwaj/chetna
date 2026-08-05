# Site assets

Static assets for the docs *site's own chrome* (logo, favicon) — not project documentation content.

`logo/chetna-logo-icon-mark-{color,white}.svg` are **copies** of the matching files in `design/assets/logo/` (the durable source of truth, see `design/assets/README.md`), not symlinks. The dockerized docs build's context is scoped to `docs/` only ([ADR-0012](../documentation/adr/0012-docs-compose-decoupling.md)) so it can't reach outside into `design/`. If the logo changes, re-copy both here manually — low-risk staleness given they're single rarely-changing static images, unlike the theme-token sync problem ADR-0013 solves for the design prototype.

Two variants, not one: Material for MkDocs' default header background is its own built-in indigo (`theme.palette.primary` isn't set, so it falls back to that default) — the *color* icon-mark reads fine on white/neutral surfaces but nearly disappears against that header, the same contrast problem hit earlier with the design prototype's app bar. `mkdocs.yml` uses the **white** variant for `theme.logo` (visible against the header) and keeps the **color** variant for `theme.favicon` (rendered small in browser chrome, not against the header, so contrast isn't an issue there).
