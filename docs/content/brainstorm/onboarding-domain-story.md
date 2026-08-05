# Onboarding domain story (draft)

**Status: brainstorm, not an approved spec.** Captures the domain-storytelling exercise done to scope the Welcome + Onboarding screens before any mockups — the first screens to be built and shipped, per the [design phase plan](../planning/design-phase-plan.md). Ordered around how a real guardian would actually move through the app (welcome → first, not persona-config-first), so it can be built and tested on a device incrementally.

## Actors

- **Guardian** — the parent/caregiver setting up the app.
- **Chetna App**.
- **SMS/OTP Provider** — external, delivers the OTP.

## The story: "A new guardian sets up Chetna for the first time"

1. Guardian downloads and opens Chetna for the first time.
2. Chetna App shows a **Welcome** screen — what the app does (proactive control, not just tracking), and a trust/privacy promise up front (no behavioral profiling, DPDP-aware), per the [v1 feature set](v1-feature-set.md) non-negotiables.
3. Guardian taps "Get started."
4. Chetna App asks for a phone number.
5. Guardian enters it and submits.
6. Chetna App requests an OTP from the SMS/OTP Provider.
7. SMS/OTP Provider delivers the OTP to the Guardian's phone.
8. Guardian enters the OTP.
9. Chetna App verifies it, creates the Guardian's account, and **silently creates a Household** for them (default name, e.g. "{Guardian's name}'s Household" — no naming step in onboarding; renameable later from settings). Per [ADR-0010](../documentation/adr/0010-authentication-and-authorization.md), phone+OTP is the only first-party credential.
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
| **1. Welcome + Auth** | Welcome → Phone entry → OTP verify → **Signed-in placeholder** (shows verified phone number, **Logout** button) | The placeholder is a throwaway test scaffold, not a real product screen — needed so login can be round-tripped (login → logout → login again) on a device before Chunks 2–4 exist. Gets replaced once Chunk 2 (Consent) and Chunk 4 (dashboard) exist. Logout itself is real and permanent; it just relocates into settings/profile later. |
| **2. Consent** | Consent screen (+ confirmation state) | Kept alone — legally load-bearing, deserves its own slice. Household auto-created as a side effect here, not a user-facing screen. |
| **3. First child** | Child name+age form → workflow suggestion (with Skip) | Crossing the "app is now usable" threshold. |
| **4. Landing** | Dashboard first-look / empty-state shell | What a Guardian sees whether or not Chunk 3's suggestion step was skipped. |

Build/ship order follows this table top to bottom, starting with Chunk 1.

## Open questions / carried-forward notes

- **Consent verification-method extensibility**: v1 consent is self-attested only (DigiLocker deferred — see [ADR-0010](../documentation/adr/0010-authentication-and-authorization.md), which already flags DigiLocker as a separate business/process dependency with its own lead time). The consent data model should carry a verification-method field (self-attested vs. externally-verified) from the start so DigiLocker can be added later additively, not as a schema rework. Not a decision to make now — just don't want it lost before the consent domain model is designed.
- **Per-device child scoping**: the Household aggregate holds multiple guardians, children, and devices, and a co-guardian joining an existing household sees everything in it. But which child a given *device* is currently associated with may, for v1, only be knowable to the app instance installed on that device — not necessarily centralized/synced across devices yet. Open architectural question for the device/credits domain modules, not resolved here.
- **Join-existing-household** (co-guardian invite flow) — deliberately out of scope for this first-run story; a separate later entry point, not part of onboarding.

## Related

- [V1 feature set](v1-feature-set.md)
- [Design phase plan](../planning/design-phase-plan.md)
- [ADR-0010: Authentication & authorization](../documentation/adr/0010-authentication-and-authorization.md)
