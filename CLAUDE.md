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

## Architecture at a glance

Chetna helps Indian parents manage children's screen time — proactive blocking, not just tracking. Three personas drive the design (own-device child, toddler on a parent-handed device, and a phase-2 shared/borrowed-device child); see `docs/content/brainstorm/v1-feature-set.md`.

This section is a map, not the source of truth. The ADRs (`docs/content/documentation/adr/`) are — read the index there before assuming anything below is still current.

**Repo layout**

| Path | What's there |
|---|---|
| `apps/domains/{consent,enforcement,credits,device}` | Kotlin Multiplatform domain modules — Clean Architecture's domain/application layers, shared across mobile and backend. No framework dependencies; only unit tests belong here (no infrastructure to integration-test against by design). |
| `apps/backend` | Spring Boot modular monolith composing the domain modules. `test`/`integrationTest`/`e2eTest` Gradle source sets; `config/` for external runtime overrides (Spring Boot's own convention, not secrets); `docs/` for code-adjacent artifacts (OpenAPI spec, diagrams) — separate from the project's main `docs/` site. |
| `apps/mobile/android` | Not yet scaffolded. Backend came first deliberately — see the sequencing note in `docs/content/brainstorm/v1-feature-set.md`. |
| `docs/` | The project's documentation/planning/brainstorm/design site (MkDocs Material), published via GitHub Pages. Has its own standalone `docs/compose.yaml` — deliberately decoupled from the root compose file so building/serving docs never needs `.env` (see ADR-0012). |
| `design/tokens` | Durable brand source of truth — seed color, shape scale, typeface, type scale, spacing. Hand-authored; the real Compose theme and the prototype's MUI adapter are both generated/derived from this, never the other way around (ADR-0013). Color-scheme generation runs on host Node, a narrowly-scoped exception alongside OpenSpec's (ADR-0014). |
| `design/assets` | Durable logo/custom-icon SVGs — outside `design/prototype/` on purpose, since that folder is disposable (ADR-0013). |
| `design/prototype` | Throwaway React + MUI mockup app — never shipped, the real app stays Kotlin/Compose per ADR-0003 (ADR-0013). Dockerized like the docs site, own standalone `compose.yaml`, included into the root file. |
| `openspec/` | Spec-driven development scaffold (OpenSpec CLI). |
| `observability/` | Prometheus/Tempo config consumed by `docker-compose.yml`. |
| `docker-compose.yml` | Local dev stack: Postgres, RabbitMQ, Temporal (+ UI), Grafana/Loki/Tempo/Prometheus, backend, plus `docs` and `design-prototype` via `include:`. Credentials come from a gitignored `.env` — never hardcode them into a compose file itself; see `.env.example`. |

**Tech stack** — each row is one ADR; read it for the actual reasoning and its "revisit when" trigger, this table is only a lookup.

| Layer | Decision | ADR |
|---|---|---|
| Mobile | Kotlin Multiplatform, native UI per platform (Compose on Android, SwiftUI on iOS later) | 0003 |
| Backend architecture | Modular monolith, DDD bounded contexts | 0004 |
| Backend stack | Kotlin/JVM, Spring Boot, PostgreSQL, REST + OpenAPI, RFC 7807 errors | 0005 |
| Workflow + events | Temporal (orchestration) + RabbitMQ (event bus, from day one) | 0006 |
| Web (phase 2) | React + TypeScript | 0007 |
| Mobile design system | Material 3 (Android) / native HIG (iOS) + custom brand theme | 0008 |
| Observability | OpenTelemetry, trace-ID correlation across HTTP/Temporal/RabbitMQ, self-hosted Grafana/Loki/Tempo/Prometheus | 0009 |
| Auth | Phone + OTP login, device pairing (not login) for child devices, plain JWT via Spring Security, no separate Authorization Server | 0010 |
| Design prototyping | Throwaway React + MUI mockup app; durable tokens generated into the real M3 color scheme via Node | 0013, 0014 |

**Testing**: TDD — the test comes before the code. Unit tests live in each module's default `test` source set. Integration tests (Testcontainers-backed, real Postgres/RabbitMQ) and end-to-end tests (against a fully running stack) exist only at the `backend` module level, never in the domain modules. `e2eTest` is never part of `check` — invoke it explicitly once the stack is up.

## Environment conventions

- Never install Python packages into the host's system/user Python — always use a venv, including inside containers (see `docs/Dockerfile` for the pattern).
- Local dev tooling (the docs site, the design prototype, and future services) runs via `docker compose`, not ad hoc host installs. Two narrowly-scoped exceptions run on host Node instead: the OpenSpec CLI (ADR-0011) and design-token color-scheme generation (ADR-0014) — both documented, both justified by nothing runtime-facing depending on them.
- This repo's git identity is the personal GitHub account (`Angesh` / `angeshbhardwaj@outlook.com`, origin over SSH via the `github-personal` key) — do not assume work-account context.
- Before pinning any dependency version in a build file, apply the `verify-dependency-version` skill — package-manager search APIs can lag behind real releases, so cross-check against authoritative registry metadata and known compatibility constraints first.
