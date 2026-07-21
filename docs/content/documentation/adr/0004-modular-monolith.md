# ADR-0004: Backend starts as a modular monolith, not microservices

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

The backend needs Clean Architecture and DDD boundaries (Consent/Guardianship, Enforcement, Credits/Chores, Device Management, and later Analytics). DDD and Clean Architecture are often conflated with microservices, but they don't require them — and the product is pre-launch, with no real scaling pressure yet.

## Options considered

- **Microservices from day one**, one deployable per bounded context. Matches DDD's bounded-context language most literally, but adds real operational overhead (service discovery, distributed tracing across process boundaries, deployment orchestration, data consistency across service boundaries) before there's any actual load or team-scaling reason to pay for it.
- **Modular monolith**: a single deployable, internally organized into modules that mirror the DDD bounded contexts, with enforced boundaries between them (no reaching across module internals). Bounded contexts stay real as a code-organization and domain-modeling discipline, without paying for distributed-systems complexity yet.

## Decision

Start with a modular monolith. Bounded contexts are enforced as internal module boundaries (e.g., separate Gradle modules), each following Clean Architecture layering internally (domain / application / infrastructure). Cross-module communication happens through explicit application-layer interfaces, not direct access to another module's internals — the same discipline needed to split a module out into its own service later, if that ever becomes necessary.

## Consequences

- Much simpler operations for a pre-launch product: one deployable, one datastore, no distributed tracing required to debug a single request.
- Bounded-context discipline is enforced by code review and module boundaries, not by physical process separation — requires actual discipline to not erode over time.
- If a specific bounded context later needs independent scaling or a different deployment cadence, the enforced module boundary makes extracting it into its own service a refactor, not a rewrite.

## Revisit when

A specific bounded context has a genuine, measured scaling or deployment-cadence need that the monolith can no longer serve (e.g. Enforcement needing to scale independently of Analytics under real production load) — not preemptively, and not because microservices are the more familiar industry pattern.
