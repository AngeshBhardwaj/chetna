# ADR-0002: Local dev tooling runs in containers, not on the host

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

We want a reproducible local dev environment from day one, rather than accumulating ad hoc host-level installs that work on one machine and not the next (or not in CI). Python in particular should never be installed/used bare on the host.

## Options considered

- **Host Python + venv only** — simpler to start, but not reproducible across machines/CI, and doesn't scale as more services (backend, web) are added.
- **Containerized services via `docker compose`, with a venv used inside each container too** — slightly more upfront setup, but reproducible everywhere Docker runs, and CI can build with the exact same image used locally.

## Decision

Any component that needs a separate deployment or a non-trivial toolchain runs via `docker compose` from day one, starting with the docs site (`docs/Dockerfile`, root `docker-compose.yml`, service `docs`). Even inside the container, dependencies install into a venv (`/opt/venv`) rather than the container's system Python — the "never bare pip" rule applies everywhere, container or not.

CI (`.github/workflows/docs.yml`) builds via `docker compose run --rm docs mkdocs build --strict`, so local and CI builds use the identical image.

## Consequences

- Local dev requires Docker (Docker Desktop with WSL integration enabled, on this machine) to be running before `docker compose up` works.
- First build per change to `requirements.txt` costs a Docker image rebuild rather than a quick `pip install`.
- Local/CI parity is strong — "works on my machine" build drift is structurally harder.

## Revisit when

If a future component genuinely cannot be containerized in a way that's more reproducible than a host install (e.g., an iOS build needing Xcode, which only runs on macOS and isn't meaningfully containerizable), document that specific exception in its own ADR rather than quietly abandoning the pattern project-wide.
