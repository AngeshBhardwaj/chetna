# ADR-0018: Transactional email delivery — SMTP port/adapter, no hardcoded provider or domain

- **Status**: Accepted
- **Date**: 2026-08-17

## Context

The real backend behind Chunk 1's OTP-verify flow ([ADR-0017](0017-identity-guardianship-domain-boundary.md)) needs to actually deliver a one-time code by email. Two constraints shape this beyond "pick a provider":

- **Chetna is open-source, and deliberately carries no company/domain details anywhere in the codebase** — a self-hoster brings their own domain and their own credentials; nothing here should assume a specific company's infrastructure.
- **OTP/transactional mail should never route through the company's own day-to-day business mailbox** (e.g. Google Workspace/Microsoft 365), even once one exists. Workspace/M365 SMTP relay is throttled for human-scale sending (roughly low-thousands/day, not built for machine-generated, per-signup volume), and mixing automated mail with `support@`/`contact@` correspondence risks the *whole* domain's sender reputation if a batch of OTP mail bounces or gets flagged as spam — that's a real, known failure mode, not a theoretical one. A dedicated transactional ESP exists precisely to isolate this risk.

## Options considered

- **Provider-specific SDK** (e.g. Brevo's or SendGrid's own client library): tightly couples the code to one vendor's API shape; a self-hoster who wants a different provider, or their own Postfix, would need a code change, not a config change.
- **Generic SMTP port/adapter** (`spring-boot-starter-mail` / `JavaMailSender`, host/port/credentials from config): every serious transactional ESP (Brevo, Mailjet, SES, SendGrid, Mailgun, Postmark) exposes an SMTP relay interface alongside their API, and so does any self-hosted mail server (Postfix, etc.). One adapter, config-driven, covers all of them — a self-hoster changes environment variables, not code.
- **Provider recommendation, for the getting-started default**: researched against two hard requirements (SMTP relay present on the free tier; no credit card required to start) — verified directly against provider pricing/SMTP pages, not an aggregator's summary. **Brevo** (300 emails/day, permanent free tier, SMTP relay included, no card) and **Mailjet** (6,000/month, permanent free tier, SMTP relay included, no card) both qualify; Mailjet's free-tier volume is actually higher. Picked Brevo as the documented default with no strong technical reason over Mailjet beyond being marginally better-known — since the adapter is provider-agnostic by construction, this is a low-stakes pick, not a lock-in.

## Decision

- A single `EmailSender`-style port in `apps/domains/identity`'s application layer, implemented in `apps/backend` via Spring's `JavaMailSender` against plain SMTP. No provider SDK dependency.
- All of host, port, credentials, and the **sending address** are runtime configuration (environment variables, following the existing `.env`/`docker-compose.yml` pattern) — never hardcoded, and `.env.example` uses a placeholder (`noreply@example.com`), not a real domain, since none is meant to live in this repo.
- **Local dev/CI**: a Mailpit (or MailHog) container added to `docker-compose.yml`, matching the existing docker-compose-first local tooling convention ([ADR-0002](0002-containerized-dev-tooling.md)) — OTP emails are inspectable in a local web UI, and e2e tests can assert on actual email content, without any real provider account or network dependency.
- **Documented default for a real deployment**: Brevo's free tier, referenced in setup docs as *a* starting point, not baked into code.
- Explicitly out of scope here: the company's own `support@`/`contact@` mailboxes. Those stay on whatever business email host the company uses, entirely separate from this system — this ADR governs only automated/transactional sending.

## Consequences

- Swapping providers (Brevo → Mailjet → SES's SMTP interface → a self-hosted Postfix) is a config change, not a code change, for any deployment of Chetna, including the original company's own.
- No sender-reputation coupling between automated OTP mail and the company's real business correspondence, once that business email exists.
- One additional local container (Mailpit) in the docker-compose stack; negligible resource cost, and it removes a real-provider dependency from local development and CI entirely.
- Rate-limiting/anti-abuse on OTP requests (flagged as an open operational concern back in [ADR-0010](0010-authentication-and-authorization.md)'s Consequences) still needs its own design — this ADR covers delivery mechanics, not abuse prevention.
- Before any real production deployment, the operator (company or self-hoster) needs a verified sending domain with SPF/DKIM/DMARC records authorizing whichever ESP they choose — a manual, DNS-side action with no code dependency, similar in shape to the Play Console developer-account signup already flagged as a user-only action in [ADR-0016](0016-play-store-distribution-strategy.md). Not blocking for building or testing this feature.

## Revisit when

Actual send volume or deliverability data suggests the documented default (Brevo's free tier) is the wrong fit for the project's own deployment — at which point it's a config change to a different SMTP target, not an architecture change. Revisit the "no rate-limiting yet" gap before this ships to any real, publicly-reachable environment.
