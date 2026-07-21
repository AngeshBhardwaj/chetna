# ADR-0006: Temporal for workflow orchestration; RabbitMQ as the event bus from day one

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

The product needs to be event-driven and implemented as workflows with a workflow engine, with strong traceability and logging (a stated non-negotiable from the outset). Candidate workflows are things like consent verification, credit-ledger reconciliation, device enrollment, and chore-approval — these are engineering-and-logic-driven flows, not processes a non-engineering business stakeholder needs to visually author or read as a diagram. Separately, modules within the modular monolith ([ADR-0004](0004-modular-monolith.md)) need to communicate as domain events (e.g. `ConsentGranted`, `CreditsEarned`) rather than direct in-process calls, so that module boundaries stay real and any future extraction into independent services is a refactor, not a rewrite.

## Options considered

- **Workflow engine — Camunda** (BPMN-first): workflows authored as visual BPMN diagrams, readable by business analysts. Strong where a non-engineering process-authoring audience exists; not the case here.
- **Workflow engine — Temporal** (code-first): workflows written as ordinary code (Kotlin/Java SDK), durable-execution guarantees, replay-based history, built-in tracing.
- **Event bus — none for now, Postgres outbox only**: minimal infrastructure, but doesn't actually deliver "event-driven from day one" — it defers the event-driven requirement rather than fulfilling it, and modules would fall back to direct calls in practice.
- **Event bus — Kafka**: high-throughput, log-based, strong streaming/replay story, but heavier to self-host and operate (partition management, higher resource footprint) than the current scale justifies.
- **Event bus — RabbitMQ**: mature, open-source (MPL 2.0), simpler to self-host than Kafka, strong AMQP support with mature Kotlin/Java clients, fits comfortably into the existing docker-compose-first local/CI convention ([ADR-0002](0002-containerized-dev-tooling.md)).

## Decision

Use **Temporal**, self-hosted (open-source), with its Java/Kotlin SDK, for durable workflow orchestration — consistent with the backend language choice in [ADR-0005](0005-backend-technology-stack.md). Use **RabbitMQ**, self-hosted (open-source), as the event bus from day one: bounded-context modules publish domain events to RabbitMQ rather than calling each other directly, and Temporal workflows are triggered by / emit those events where a durable, long-running process is involved.

## Consequences

- The system is event-driven in substance from the first version, not just in aspiration — module boundaries are enforced through published events, not direct calls, which also makes any future service extraction under [ADR-0004](0004-modular-monolith.md) more mechanical.
- Two additional pieces of infrastructure to run and operate from day one (Temporal server, RabbitMQ), both self-hosted and added to the docker-compose setup.
- Workflows and event-driven module communication both live in the same backend language/codebase, with no separate BPMN authoring tool to keep in sync with code.

## Revisit when

Message volume or the need for durable log-based replay/streaming analytics outgrows what RabbitMQ comfortably handles — that's the concrete trigger to evaluate Kafka, not a preemptive switch. Revisit the Temporal-vs-Camunda choice specifically only if a real non-engineering audience emerges that needs to read/author these workflows as BPMN diagrams.
