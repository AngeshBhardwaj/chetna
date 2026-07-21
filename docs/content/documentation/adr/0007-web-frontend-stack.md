# ADR-0007: Web frontend stack — React + TypeScript

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

The web portal (parent dashboard, login, account management, analytics) is a phase-2 deliverable, deferred behind mobile-only v1. Unlike mobile ([ADR-0003](0003-mobile-architecture.md)), the constraint that ruled out a cross-platform UI framework — deep, unrestricted OS API access — doesn't apply to a web dashboard, which is fundamentally forms, authentication, and analytics charts.

## Options considered

- **Compose Multiplatform for Web** (Kotlin/Wasm): would preserve the "Kotlin end-to-end" consistency established by [ADR-0003](0003-mobile-architecture.md)/[ADR-0005](0005-backend-technology-stack.md) and could reuse KMP domain code directly. But it's the least mature of JetBrains' Compose targets — smaller ecosystem, fewer accessibility and charting libraries, smaller hiring pool.
- **React + TypeScript**: the mainstream choice for dashboard-style web apps. Mature component, charting, and accessibility libraries; large hiring pool. Breaks from the Kotlin consistency, but doesn't need to share code with mobile/backend directly — it talks to the backend via the OpenAPI contract already chosen in [ADR-0005](0005-backend-technology-stack.md), with a generated TypeScript client giving strong typing without shared Kotlin code.

## Decision

React + TypeScript for the web dashboard, when phase 2 work begins. The OpenAPI contract is the integration point with the backend, not shared Kotlin code.

## Consequences

- Web work is a genuinely separate codebase/language from mobile and backend — no domain-logic reuse across the three, unlike the mobile/backend sharing enabled by KMP.
- Gains the much larger React ecosystem for exactly the things a dashboard needs most: accessible components, analytics charting libraries, and a larger hiring pool.
- The OpenAPI contract becomes the load-bearing integration point between web and backend — worth keeping it accurate and versioned carefully once web work starts.

## Revisit when

Compose Multiplatform for Web's ecosystem (charting, accessibility, component libraries) matures substantially before phase 2 web work actually begins — otherwise, not worth relitigating once React work has started.
