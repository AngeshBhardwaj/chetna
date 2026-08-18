# ADR-0017: Identity/Guardianship as its own bounded context, separate from Consent

- **Status**: Accepted
- **Date**: 2026-08-17

## Context

Chunk 1's mobile screens (Welcome, Contact details, OTP verify, Signed-in) shipped with mocked auth — see [ADR-0015](0015-android-app-architecture.md). Building the real backend behind them means deciding, for the first time, where "Guardian account exists, is authenticated, belongs to a Household" actually lives as a bounded context. [ADR-0010](0010-authentication-and-authorization.md) currently says sensitive operations are "gated on consent-state... tying directly to the Consent/Guardianship bounded context already established as first-class" — treating identity/guardianship and DPDP consent as one thing. That framing needs revisiting now that real modules are being built, not just planned.

Two other decisions from this same planning round shape the model:
- The **Household** is created at **consent-grant time** (Chunk 2), not at OTP-verify time (Chunk 1) — there's no benefit to creating it earlier, and it keeps this chunk's backend scope matching the mobile screens that actually exist today (Guardian account + login only, no consent yet).
- **Multi-guardian households** are a first-class part of the data model from day one, even though only one guardian is created during this first-run flow — retrofitting a single-guardian assumption later is more expensive than modeling it correctly now. The co-guardian *invite/join* flow itself stays out of scope (already deferred in the [onboarding domain story](../../brainstorm/onboarding-domain-story.md)'s Open Questions) — only the data shape needs to support it.

## Options considered

- **Fold Guardian/Household/Auth into `apps/domains/consent`**: matches ADR-0010's current wording literally. Rejected — DPDP consent (what data is collected about a child, why, self-attested vs. externally-verified) and identity/guardianship (who is this guardian, what household do they belong to, how do they authenticate) are genuinely different concerns with different lifecycles: a guardian can exist and log in without having granted consent yet (the exact state Chunk 1 leaves them in), and consent is granted *by* an already-identified guardian, not the other way around. Conflating them into one module would mean the consent module reaching into concerns (JWT issuance, OTP challenge/attempt state, session/refresh tokens) that have nothing to do with DPDP compliance.
- **New `apps/domains/identity` module**: Guardian, Household, the Guardian↔Household membership (with a role — Owner/Co-Guardian, per ADR-0010's authorization model), OTP challenge state, and session/refresh tokens. `apps/domains/consent` later depends on this module's Guardian/Household concepts (e.g. "which guardian is granting consent, for which household") the same way any other bounded context would reference an identity, not the reverse.

## Decision

Add a fifth Kotlin Multiplatform domain module, `apps/domains/identity`, following the exact pattern already established by `consent`/`enforcement`/`credits`/`device` (the `chetna.kotlin-multiplatform-domain` convention plugin, Clean Architecture domain/application layers, no framework dependencies, unit tests only — no infrastructure to integration-test against by design).

Aggregates/entities owned by this module:
- **Guardian** — id, email, email-verified flag, created-at. No Household reference on Guardian itself.
- **Household** — id, name (default-generated, renameable later), created-at. Not created until Chunk 2 (consent-grant time); modeled now so the eventual migration is designed correctly, not populated yet.
- **HouseholdMembership** — join entity: guardianId, householdId, role (Owner | Co-Guardian), joined-at. Exists specifically so a household can have more than one guardian from day one, even though Chunk 1/2 only ever create one membership row (the Owner, at consent-grant time).
- **OtpChallenge** — id, email, code hash (never the raw code), expires-at, attempts-remaining, status (Pending/Verified/Locked), purpose (Login, for now — extensible if OTP is reused elsewhere later). Mirrors the 3-attempt-then-lockout behavior the mobile `OtpViewModel` already implements, so the backend is the source of truth the mobile UI's mocked logic gets replaced with.
- **Session/RefreshToken** — id, guardianId, token hash, expires-at, revoked-at, issued-at. Supports logout (revoke) without needing a full OAuth2 Authorization Server, consistent with ADR-0010's plain-JWT decision.

`apps/domains/consent` is unaffected by this change beyond depending on `identity`'s Guardian/Household types once consent work starts.

## Consequences

- ADR-0010's authorization model (role-based, scoped to the Household aggregate) now has a concrete home for where "role" and "Household" actually live — `identity`, not `consent`.
- A guardian can exist in a fully authenticated, no-consent-yet state — which is exactly Chunk 1's real end state once this backend ships, and is now a legitimate, modeled state rather than an edge case.
- `apps/settings.gradle.kts` gains a fifth `include(":domains:identity")` entry; the `apps/backend` module's dependency list grows by one line, matching the existing four.
- ADR-0010 needs a short in-place amendment (see below) so its "tying directly to the Consent/Guardianship bounded context" phrasing doesn't contradict this module split.

## Revisit when

The Household aggregate needs to model more than membership + role — e.g. per-child device scoping that spans households (already flagged as an open architectural question in the onboarding domain story, not resolved here). Or if `identity` and `consent` end up needing to change together often enough in practice that the module boundary is producing friction rather than clarity — not preemptively.
