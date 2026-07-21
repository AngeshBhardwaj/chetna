# ADR-0005: Backend technology stack — Kotlin/JVM + Spring Boot, PostgreSQL, REST/OpenAPI, OpenTelemetry

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

With Kotlin Multiplatform chosen for mobile ([ADR-0003](0003-mobile-architecture.md)), the backend language choice affects how much of the domain model (entities, validation rules, value objects) can be genuinely shared with mobile rather than reimplemented per language. The backend also needs a database, an API contract style consistent with spec-driven development (OpenSpec), and an observability approach consistent with the project's "fully open-source, self-hosted, docker-compose-first" conventions ([ADR-0002](0002-containerized-dev-tooling.md)).

## Options considered

- **Language/runtime**: Kotlin/JVM vs. Node.js/TypeScript, Python, or Go. Kotlin/JVM lets the domain module built for mobile ([ADR-0003](0003-mobile-architecture.md)) be reused directly on the backend via the same KMP module — the other options would require reimplementing domain rules in a second language.
  - **Python/FastAPI specifically** was considered because of the phase-2 face-recognition work (see the [v1 feature set](../../brainstorm/v1-feature-set.md)), given Python's ML ecosystem. But that work runs **on-device only** (a bundled TFLite model, inference on the child's phone) — the backend never touches face data or runs inference, which is precisely what keeps the DPDP compliance risk low. The only place Python's ML ecosystem would actually matter is the offline model *training/fine-tuning* step, which doesn't need to share a runtime with the production backend at all — it can stay a standalone Python pipeline that just outputs a `.tflite` file, regardless of backend language. FastAPI's native OpenAPI generation and larger hiring pool were real points in its favor, but not enough to give up the concrete KMP domain-sharing benefit for the core backend.
- **Web framework: Ktor vs. Spring Boot.** The KMP domain-sharing benefit above is orthogonal to this choice — the shared module is framework-agnostic plain Kotlin, reused identically whether it's wrapped by Ktor routes or Spring Boot controllers, so "staying pure Kotlin" isn't actually an argument for Ktor specifically. The deciding factors were practical: prior hands-on experience with Spring Boot (none with Ktor) directly reduces risk on a project already adopting many new things at once (KMP, Temporal, RabbitMQ, DDD, TDD, OpenSpec); Spring Security is far more mature than Ktor's auth story, relevant given how compliance-sensitive the guardian-consent/parent-auth flows are; Micrometer + Spring Boot Actuator gives near-zero-config OpenTelemetry integration; `springdoc-openapi` auto-generates the OpenAPI contract from code, where Ktor's OpenAPI story is comparatively manual. Ktor's real advantages (lighter weight, faster cold starts) matter most for serverless-style deployments, not a persistently-running modular monolith.
- **Database**: PostgreSQL — mature, open-source, relational with JSONB support for flexible/evolving schemas. No serious alternative considered given the requirement is simply "solid open-source relational store," not a specialized data shape.
- **API style**: REST with an OpenAPI contract vs. GraphQL vs. gRPC-only. REST + OpenAPI is simplest for mobile clients, has the widest tooling support, and pairs naturally with spec-driven development (the OpenAPI contract as an artifact of the spec). gRPC remains an option for internal service-to-service calls if the system is ever split per [ADR-0004](0004-modular-monolith.md), but isn't needed for a modular monolith's external API surface.
- **Observability**: OpenTelemetry (vendor-neutral, integrates with Temporal's own tracing — see [ADR-0006](0006-workflow-orchestration.md)) exporting to a self-hosted Grafana/Loki/Tempo/Prometheus stack, vs. a commercial SaaS observability product. Self-hosted OSS keeps the "fully open-source" constraint intact and fits the existing docker-compose-first local/CI pattern.
- **Error handling**: RFC 7807 (Problem Details for HTTP APIs) for a consistent, structured error schema across the API surface, with domain-layer exceptions translated at the application-layer boundary rather than leaking internal exception types to clients.
- **Testing**: JUnit5 + Kotest + MockK on the JVM side.

## Decision

Kotlin/JVM on **Spring Boot** backend, PostgreSQL, REST API with an OpenAPI contract (via `springdoc-openapi`) and RFC 7807 error responses, OpenTelemetry (via Micrometer + Spring Boot Actuator) exporting to a self-hosted Grafana/Loki/Tempo/Prometheus stack, JUnit5/Kotest/MockK for testing.

## Consequences

- Domain logic can be shared between mobile and backend through the same KMP module, reducing duplicate business-rule implementations and the drift risk that comes with them — this holds regardless of the Ktor/Spring Boot choice, since the shared module is framework-agnostic.
- Inherits Spring Boot's much larger ecosystem and hiring pool relative to Ktor's, plus mature, battle-tested Security/Actuator/OpenAPI tooling that maps directly onto this project's compliance and observability needs.
- Spring's DI/annotation-heavy conventions are more "magic" than Ktor's plugin model, but this is a familiar, well-understood trade-off given prior hands-on experience with the framework.

## Revisit when

A specific capability gap makes Kotlin/JVM or Spring Boot untenable for a concrete requirement (e.g. a genuine need for the lighter-weight, faster-cold-start profile Ktor offers, such as a serverless-style extraction of a bounded context) — not preemptively.
