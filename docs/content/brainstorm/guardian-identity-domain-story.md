# Guardian identity domain story (draft)

**Status: brainstorm, not an approved spec.** Captures the domain-storytelling exercise for the real backend behind Chunk 1's already-shipped, mocked-auth mobile screens (Welcome → Contact details → OTP verify → Signed-in placeholder — see the [onboarding domain story](onboarding-domain-story.md)). That doc covers the mobile UX; this one covers what the backend actually has to do and hold, following the module boundary decided in [ADR-0017](../documentation/adr/0017-identity-guardianship-domain-boundary.md).

## Actors

- **Guardian** — the parent/caregiver.
- **Chetna Backend** — specifically the `identity` bounded context.
- **Email delivery** — the SMTP port described in [ADR-0018](../documentation/adr/0018-transactional-email-delivery.md); an external actor from the domain's point of view, same as the Email/OTP Provider in the mobile-facing story.

## The story: "A guardian's device asks the backend to prove who they are"

1. Guardian enters their email on the Contact details screen and submits.
2. Backend receives a request to start login for that email. It creates an **OtpChallenge**: a 6-digit code (hashed before storage — the raw code is never persisted), an expiry, `attemptsRemaining` starting at 3, status `Pending`, purpose `Login`.
3. Backend asks the email port to deliver the raw code to the guardian's email. The port doesn't know or care which provider is behind it — see ADR-0018.
4. Guardian enters the code into the mobile app's 6 boxed digit fields (already built, auto-advancing, auto-verifying).
5. Backend receives a verify request for that email + code.
6. **If the code matches and the challenge hasn't expired**: the challenge moves to `Verified`. If no Guardian record exists yet for this email, one is created (id, email, `emailVerified = true`, `createdAt`) — this is the guardian's *first* login, not just a repeat one. A session is opened: a refresh token is issued (hashed before storage, like the OTP code), and a short-lived JWT access token is returned to the mobile app.
7. **If the code doesn't match**: `attemptsRemaining` decrements. Above 0, the challenge stays `Pending` and the mobile app clears the boxes for retry (already-built behavior). At 0, the challenge moves to `Locked` — no further verify attempts against this challenge succeed, matching the mobile UI's existing lockout screen, which points the guardian back to requesting a new code.
8. Guardian reaches the mobile app's Signed-in placeholder, now backed by a real, persisted Guardian and an active session — not a `MOCK_CORRECT_CODE` string.
9. Guardian taps Logout. Backend revokes the refresh token (`revokedAt` set) for that session. The access token itself is short-lived and simply expires; nothing to revoke there.

No Household exists yet at the end of this story — that's created at consent-grant time (Chunk 2), per the decision recorded in ADR-0017. A Guardian who has verified their email but not yet granted consent is a real, expected state, not an edge case to special-case around.

## What this chunk does *not* do

- **Household / HouseholdMembership creation** — modeled in `identity` (per ADR-0017) but populated starting in the Consent chunk, not this one.
- **Google Sign-In** — deferred as its own fast-follow slice (needs a new mobile UI element plus backend OAuth2 client wiring); email+OTP is the only login path this chunk builds.
- **Device pairing** — a completely separate flow (ADR-0010) for a child's device, not a guardian login concern.
- **Rate-limiting / anti-abuse on OTP requests** — flagged as an open operational concern in both ADR-0010 and ADR-0018; needs its own design before this is exposed anywhere beyond local dev, but isn't resolved here.

## Open questions / carried-forward notes

- **OTP resend vs. new challenge**: the mobile `OtpViewModel` already has resend behavior (30s cooldown, doesn't reset `attemptsRemaining`) implemented against a mock. The backend needs to decide: does resend reuse the *same* `OtpChallenge` row (new code, same attempts budget) or create a fresh one? Leaning toward reusing the same row — matches the mobile behavior's "doesn't reset attempts" already-validated design — but not decided here; the actual OpenSpec change's design.md should settle this with a concrete reason, not just match-the-mobile-app-by-default.
- **Challenge expiry duration**: not yet picked. Needs a value short enough to limit the window an intercepted code is useful, long enough that a slow guardian doesn't get expired mid-entry.
- **Multiple concurrent devices**: if a guardian starts login on two devices at once, does the second `OtpChallenge` request invalidate the first, or can both be pending simultaneously? Not resolved here — affects whether `OtpChallenge` is keyed uniquely per email or allows multiple pending rows.

## Related

- [Onboarding domain story](onboarding-domain-story.md) — the mobile-facing version of this same first-run flow.
- [ADR-0010: Authentication & authorization](../documentation/adr/0010-authentication-and-authorization.md)
- [ADR-0017: Identity/Guardianship domain boundary](../documentation/adr/0017-identity-guardianship-domain-boundary.md)
- [ADR-0018: Transactional email delivery](../documentation/adr/0018-transactional-email-delivery.md)
