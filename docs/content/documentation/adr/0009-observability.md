# ADR-0009: Observability — structured logging, trace correlation, dashboards & alerting

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

[ADR-0005](0005-backend-technology-stack.md) named the observability tools (OpenTelemetry, Micrometer + Spring Boot Actuator, self-hosted Grafana/Loki/Tempo/Prometheus) but not the actual strategy for using them. "Good traceability, logs" was a stated non-negotiable from the outset, and it's harder to deliver than usual here because a request's lifecycle doesn't stay inside one synchronous call — it crosses into Temporal workflows (which can span minutes to days) and RabbitMQ messages ([ADR-0006](0006-workflow-orchestration.md)). Without deliberate propagation, trace context is lost exactly at those boundaries — precisely where debugging a real incident would need it most.

## Options considered

- **Unstructured/plain-text logs**, correlated manually (e.g. by timestamp/eyeballing) across logs, traces, and metrics. Simplest to set up, but breaks down fast once workflows and async messaging are involved — which is most of this system.
- **Structured JSON logs with trace-ID correlation**: every log line carries the active OpenTelemetry trace ID (via MDC in Logback), and that trace context is deliberately propagated through Temporal workflow inputs and RabbitMQ message headers, not just left to work by accident within a single HTTP request.

## Decision

- **Logging**: JSON-structured logs via Logback, with OpenTelemetry trace ID injected via MDC on every log line.
- **Trace propagation across async boundaries**: trace context is explicitly carried in Temporal workflow/activity inputs and RabbitMQ message headers, so a single logical operation (e.g. "process this consent verification") stays traceable end-to-end even as it moves between the API layer, a workflow, and a queued event.
- **Metrics & dashboards**: Micrometer + Spring Boot Actuator export metrics; Grafana for dashboards, backed by Prometheus (metrics), Loki (logs), and Tempo (traces) — all self-hosted, consistent with the "fully open-source" constraint and the existing docker-compose-first convention ([ADR-0002](0002-containerized-dev-tooling.md)).
- **Alerting**: Prometheus Alertmanager or Grafana's own alerting, on top of the same self-hosted stack.

## Consequences

- A single request/workflow/event can be followed end-to-end across logs, traces, and metrics, including through Temporal and RabbitMQ — the specific case that would otherwise silently break traceability.
- Requires deliberate propagation code at every workflow/message boundary, not just default framework behavior — an ongoing discipline, not a one-time setup.
- One more self-hosted stack (Grafana/Loki/Tempo/Prometheus) to run in every environment, including local dev via docker-compose.

## Revisit when

Log/metric/trace volume outgrows what a self-hosted Grafana/Loki/Tempo/Prometheus stack can handle comfortably — that's the concrete trigger to evaluate a managed or higher-scale alternative, not a preemptive switch.
