# ADR-0016: Play Store distribution & target-audience strategy

- **Status**: Accepted
- **Date**: 2026-08-06

## Context

The user wants Chunk 1 (Welcome + Auth) taken all the way to something installable, with an eye toward eventual real Play Store distribution. Chetna's entire premise is processing data *about* children, which raises a real Google Play Families-policy question before any store presence is set up — but the app itself is installed and operated by the guardian; per [ADR-0010](0010-authentication-and-authorization.md), the child never has independent login, only device pairing. Getting the target-audience classification wrong has real consequences (the Families program imposes ad-SDK restrictions, neutral age screens, and stricter data rules that don't fit a guardian-operated control app).

## Options considered

- **Target audience declaration — children vs. adults**: verified directly against Google Play Console's own help documentation (2026) that Families-policy compliance is triggered by *declared target audience including children* in Play Console, not merely by an app processing data that relates to children. Established parental-control apps (Google Family Link, Qustodio, Bark) operate this way today — adult target audience, with children's-data handling disclosed honestly in the mandatory Data Safety section, not enrolled in the Families program. Chosen: **adults/guardians** — matches both the actual primary user/installer and ADR-0010's device-pairing-not-login model. Declaring "children" was rejected — it would misrepresent who actually operates the app and pull in ad-SDK/neutral-age-screen requirements that don't apply.
- **Distribution path — straight to production vs. a ladder**: a new *personal* Google Play developer account must complete a closed test with a minimum of 12 testers opted in for 14 *consecutive* days before any production-track access is even possible — verified directly against Play Console's testing-requirements documentation. Going straight for a public listing isn't actually available even if desired. Chosen: a four-rung ladder (below), not a single jump.
- **When to start the Play Console developer account**: now vs. deferred until the app is more complete. The $25 one-time fee plus identity verification (document upload, sometimes a selfie, "a few hours to 2 business days" per Google's own guidance) is pure external lead time requiring the user's own real identity and payment — something only they can do, with zero technical dependency on how much of the app exists yet. Chosen: the user can start this whenever they want, in parallel with engineering work, since it doesn't block or get blocked by anything technical.

## Decision

- **Target audience**: adults/guardians. The Data Safety section still fully and honestly discloses child-related data processing regardless of this declaration — this is an honest classification of who operates the app, not a way to avoid disclosure obligations.
- **Distribution ladder**, in order:
  1. **Local APK sideload + Dockerized emulator** — available now, zero external dependency (see [ADR-0015](0015-android-app-architecture.md)).
  2. **Play Console internal testing** — once a developer account exists and there's a build worth sharing beyond the user's own device (up to 100 testers, no minimum, no wait, near-instant releases).
  3. **Closed testing** — 12 testers, 14 consecutive days, the mandatory gate for new personal accounts before production access. Only worth starting once the app has a real feature, not just a login screen — asking testers to sit through 14 days for Chunk 1 alone isn't a good use of that gate.
  4. **Production/public listing** — deferred until Chunk 2 (Consent) content exists, since the Data Safety section needs real, finalized data-collection decisions to be accurate, and until at least one real feature is functional.
- **Developer account signup**: a user-only action, can start any time, doesn't block or wait on engineering work.

## Consequences

- No Play Store policy surprises later — the classification is verified against Google's own current documentation, not assumed by analogy alone.
- Chunk 1 alone will never reach production/public listing under this plan, by design — that's correct, not a gap, given it's just a login flow with no functioning feature yet.
- The 12-tester/14-day closed-testing gate is a real, fixed lead time once it starts — worth the user starting the developer-account signup early precisely because that part has no technical dependency, even though closed testing itself should wait for more features.
- If Chetna's positioning or persona model changes later (e.g. a phase-2 feature that puts more UI directly in a child's hands, per the deferred "Advanced blocking mode" in the v1 feature set), the target-audience classification would need re-examination — not assumed to hold forever.

## Revisit when

Chunk 2 (Consent) content is finalized — write the real Data Safety section content then, informed by the actual data model, not before. Or sooner, if Play Console's own target-audience questionnaire (filled out by the user directly, on their own account) surfaces something unexpected that contradicts the reasoning above.
