# Architecture Decision Records

An ADR captures a single architecturally-significant decision: what we chose, why, what it costs us, and — critically — the concrete condition under which we should reopen it. That last part keeps decisions from being re-litigated on a whim, while still making sure they get revisited when the reason for them stops being true.

## Process

1. Copy [`template.md`](template.md) to `NNNN-short-title.md` (next sequence number, zero-padded).
2. Fill it in. Status starts as `Proposed` until agreed, then `Accepted`.
3. If a later ADR overturns an earlier one, mark the old one `Superseded by ADR-NNNN` — don't delete it. The history of *why* we changed course is as valuable as the current decision.

## Index

| ADR | Title | Status |
|-----|-------|--------|
| [0001](0001-docs-site-tooling.md) | Docs site built with MkDocs Material | Accepted |
| [0002](0002-containerized-dev-tooling.md) | Local dev tooling runs in containers, not on the host | Accepted |
| [0003](0003-mobile-architecture.md) | Mobile app built with Kotlin Multiplatform + native UI per platform | Accepted |
| [0004](0004-modular-monolith.md) | Backend starts as a modular monolith, not microservices | Accepted |
| [0005](0005-backend-technology-stack.md) | Backend technology stack — Kotlin/JVM + Spring Boot, PostgreSQL, REST/OpenAPI, OpenTelemetry | Accepted |
| [0006](0006-workflow-orchestration.md) | Temporal for workflow orchestration; RabbitMQ as the event bus from day one | Accepted |
| [0007](0007-web-frontend-stack.md) | Web frontend stack — React + TypeScript | Accepted |
| [0008](0008-mobile-design-system.md) | Mobile design system — platform-native + custom brand theme | Accepted |
| [0009](0009-observability.md) | Observability — structured logging, trace correlation, dashboards & alerting | Accepted |
| [0010](0010-authentication-and-authorization.md) | Authentication & authorization — phone+OTP login, device pairing, JWT via Spring Security | Accepted |
| [0011](0011-openspec-host-install-exception.md) | OpenSpec/Node installed natively on the host — exception to ADR-0002 | Accepted |
| [0012](0012-docs-compose-decoupling.md) | Docs site gets its own standalone compose file | Accepted |
| [0013](0013-design-prototype-tooling.md) | Design prototype tooling — durable tokens vs. throwaway MUI mockup app | Accepted |
| [0014](0014-design-token-generation-runtime.md) | Design-token color generation runs on Node, not Kotlin | Accepted |
