# Onboarding domain story (draft)

**Status: brainstorm, not an approved spec.** Captures the domain-storytelling exercise done to scope the Welcome + Onboarding screens before any mockups — the first screens to be built and shipped, per the [design phase plan](../planning/design-phase-plan.md). Ordered around how a real guardian would actually move through the app (welcome → first, not persona-config-first), so it can be built and tested on a device incrementally.

## Actors

- **Guardian** — the parent/caregiver setting up the app.
- **Chetna App**.
- **Email/OTP Provider** — external, delivers the OTP.

## The story: "A new guardian sets up Chetna for the first time"

1. Guardian downloads and opens Chetna for the first time.
2. Chetna App shows a **Welcome** screen — what the app does (proactive control, not just tracking), and a trust/privacy promise up front (no behavioral profiling, DPDP-aware), per the [v1 feature set](v1-feature-set.md) non-negotiables.
3. Guardian taps "Get started."
4. Chetna App asks how to reach the Guardian — **email is the v1 default**; phone/SMS is shown but disabled ("Coming soon"), not hidden, so it doesn't read as a permanent gap. See ADR-0010's 2026-08-05 amendment: SMS-OTP costs money per message from day one, email doesn't.
5. Guardian enters their email, checks a lightweight "I agree to receive a verification code at this email address" consent (transactional only — separate from the DPDP guardian-consent screen in step 10), and submits.
6. Chetna App requests an OTP from the Email/OTP Provider.
7. Email/OTP Provider delivers the OTP to the Guardian's email.
8. Guardian enters the 6-digit OTP into 6 boxed digit fields, which auto-advance focus and auto-verify once all 6 are filled — no explicit "Verify" button.
9. Chetna App verifies it, creates the Guardian's account, and **silently creates a Household** for them (default name, e.g. "{Guardian's name}'s Household" — no naming step in onboarding; renameable later from settings). Per [ADR-0010](../documentation/adr/0010-authentication-and-authorization.md) (amended 2026-08-05), email+OTP is the v1 default and only first-party credential. If the code is wrong, the Guardian gets up to 3 attempts, clearing the boxes and retrying each time; a 4th wrong attempt locks the screen and points back to requesting a new code.
10. Chetna App shows the **guardian-consent** screen — DPDP-mandated, explains what data is collected about the child and why. Self-attested for v1 (see [Open questions](#open-questions-carried-forward-notes) — DigiLocker deferred).
11. Guardian reviews and gives consent.
12. Chetna App asks the Guardian to add a **child profile** — name and age group. This is the minimum required for the app to actually function: one Guardian + one child.
13. Guardian selects the age group.
14. Chetna App suggests a workflow based on age group — toddler kids-mode vs. own-device schedule/credits — framed as a *suggestion*, not imposed.
15. Guardian accepts, overrides, or **skips** the suggestion (deferred to later, reachable from the dashboard).
16. Chetna App hands off to a signed-in state — either that workflow's first-run setup, or straight to the dashboard shell.

## Mandatory spine vs. deferred branches

**Mandatory** (must complete before the app is usable): steps 2–13 — Welcome through adding one child. Consent (10–11) is not skippable; it's a legal requirement, not a UX nicety.

**Deferred, each reachable later with a Skip:**

- Workflow suggestion / acceptance (14–15).
- Workflow-specific first-run setup (allow-list, schedule, etc.).
- **OS permissions** (notifications, Accessibility, Device Admin, etc.) — not part of onboarding at all. Requested contextually the first time a feature that needs them is actually used (e.g. Accessibility only when kids-mode is first turned on), not upfront.

A Guardian can explore the app shell immediately after account + consent, even without a child yet or with the workflow suggestion skipped — but feature screens (kids-mode, schedule) stay in an empty/nudge state ("Add a child to get started") until a child actually exists.

## Build chunks (2–3 screens each, buildable/shippable independently)

| Chunk | Screens | Notes |
|---|---|---|
| **1. Welcome + Auth** | Welcome → Contact details (email) → OTP verify → **Signed-in placeholder** (shows verified email, **Logout** button) | The placeholder is a throwaway test scaffold, not a real product screen — needed so login can be round-tripped (login → logout → login again) on a device before Chunks 2–4 exist. Gets replaced once Chunk 2 (Consent) and Chunk 4 (dashboard) exist. Logout itself is real and permanent; it just relocates into settings/profile later. |
| **2. Consent** | Consent screen (+ confirmation state) | Kept alone — legally load-bearing, deserves its own slice. Household auto-created as a side effect here, not a user-facing screen. |
| **3. First child** | Child name+age form → workflow suggestion (with Skip) | Crossing the "app is now usable" threshold. |
| **4. Landing** | Dashboard first-look / empty-state shell | What a Guardian sees whether or not Chunk 3's suggestion step was skipped. |

Build/ship order follows this table top to bottom, starting with Chunk 1.

Chunk 1's screens were reworked against externally-sourced native-Android-style reference mockups (PNGs + exported HTML/CSS, `design/prototype/reference/onboarding/` — see ADR-0013's rule on handling external design ideas) to close a real fidelity gap the first pass had: oversized in-flow headers, no icon-based hero/status illustrations, a plain text field instead of boxed OTP entry, and inconsistent button widths. Three patterns from that rework became shared prototype components (`design/prototype/src/shared/`) rather than one-off styling, since they're the baseline for every later screen, not just this chunk: `IconBadge` (M3's large-icon-in-tonal-container hero/status pattern), `SelectionCard` (the M3 selection-row pattern, leading icon + title/subtitle + trailing radio), and `OtpInputGroup` (boxed, auto-advancing, auto-verifying OTP entry with a 3-attempt wrong-code retry before locking).

## Open questions / carried-forward notes

- **Consent verification-method extensibility**: v1 consent is self-attested only (DigiLocker deferred — see [ADR-0010](../documentation/adr/0010-authentication-and-authorization.md), which already flags DigiLocker as a separate business/process dependency with its own lead time). The consent data model should carry a verification-method field (self-attested vs. externally-verified) from the start so DigiLocker can be added later additively, not as a schema rework. Not a decision to make now — just don't want it lost before the consent domain model is designed.
- **Per-device child scoping**: the Household aggregate holds multiple guardians, children, and devices, and a co-guardian joining an existing household sees everything in it. But which child a given *device* is currently associated with may, for v1, only be knowable to the app instance installed on that device — not necessarily centralized/synced across devices yet. Open architectural question for the device/credits domain modules, not resolved here.
- **Join-existing-household** (co-guardian invite flow) — deliberately out of scope for this first-run story; a separate later entry point, not part of onboarding.

## Related

- [V1 feature set](v1-feature-set.md)
- [Design phase plan](../planning/design-phase-plan.md)
- [ADR-0010: Authentication & authorization](../documentation/adr/0010-authentication-and-authorization.md) (see its 2026-08-05 amendment for the email-OTP-first decision)
- [ADR-0013: Design prototype tooling](../documentation/adr/0013-design-prototype-tooling.md) (external design ideas handling)
