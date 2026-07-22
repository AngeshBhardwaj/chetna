# ADR-0012: Docs site gets its own standalone compose file

- **Status**: Accepted
- **Date**: 2026-07-22

## Context

[ADR-0010](0010-authentication-and-authorization.md)'s era of work added `${VAR:?...}` required-variable guards to the root `docker-compose.yml` so real credentials (Postgres, RabbitMQ, Temporal DB, Grafana admin) never sit hardcoded in a tracked file — they live in a gitignored `.env` instead. This broke the docs-publishing GitHub Actions workflow: `docker compose run --rm docs mkdocs build --strict` failed with `required variable TEMPORAL_DB_PASSWORD is missing a value`, because Compose interpolates variables for the *entire* file when parsing it, not just the service actually being run — and CI has no `.env` (correctly, since it's gitignored and was never meant to exist there).

## Options considered

- **Give CI a throwaway `.env`** (a workflow step writing dummy values before the build). Works, but conflates "build the documentation site" with "know about backend/infra credentials it never touches" — and the coupling would resurface every time a new required variable is added to any unrelated service.
- **Loosen the required-variable guards** back to soft defaults. Rejected outright — that undoes the exact hardening `.env` was introduced for.
- **Rely on Compose profiles** to exclude the other services from interpolation when not activated. Investigated and rejected: whether profile-gating skips required-variable interpolation for inactive services is inconsistent/version-dependent based on available documentation and issue reports — not something to build on without clearer confirmation.
- **Give the docs service its own standalone compose file** (`docs/compose.yaml`), included into the root file via Compose's `include:` directive (v2.20.0+, confirmed available — this repo's Compose is v5.3.0). The docs site was never actually related to the backend/infra stack; it just happened to share one file with it.

## Decision

`docs/compose.yaml` is a standalone compose file for the docs service only — no required variables, no `.env` dependency. The root `docker-compose.yml` uses `include: [docs/compose.yaml]` so a plain `docker compose up` from the repo root still brings up docs alongside everything else for local full-stack use. The GitHub Actions workflow (`.github/workflows/docs.yml`) targets `docs/compose.yaml` directly via `-f`, never going through the root file, so it never needs `.env` at all.

## Consequences

- CI for docs publishing has zero coupling to backend/infra secrets, present or future.
- Two compose files exist rather than one — `docker-compose.yml` (root, full stack) and `docs/compose.yaml` (docs only). Deliberately different names (not two files both called `docker-compose.yml`) to avoid confusion about which is which.
- `docker compose up docs` from the repo root still requires `.env` (since the root file's other services still need it for interpolation) — the standalone, `.env`-free path is `docker compose -f docs/compose.yaml up`, which is what's documented as primary in the root `README.md`.

## Revisit when

Compose's handling of required variables under inactive profiles becomes clearly and consistently documented as skipping interpolation — that would remove the need for a separate file. Not before; don't build on unconfirmed behavior.
