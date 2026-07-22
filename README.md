# chetna

An app to help Indian parents get their children's screen time under control — not just track it, but actively enforce it.

## Repository layout

- `apps/` — application code (mobile, backend, web). Empty until the tech stack is decided.
- `docs/` — project documentation, planning, research, brainstorming, and design, published as a [GitHub Pages](https://pages.github.com/) site via MkDocs Material.

## Working on the docs site

```bash
docker compose -f docs/compose.yaml up
```

Then open http://localhost:8000. The site auto-reloads as you edit files under `docs/content/`. This is deliberately a standalone compose file (no `.env` required) — the docs site has nothing to do with the application's backend/infra services. It's also included into the root `docker-compose.yml` (via `include:`), so `docker compose up` from the repo root brings it up too, alongside everything else, if you're running the full stack and already have `.env` set up.
