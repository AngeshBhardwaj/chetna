# Competitive landscape & feasibility research

Research date: 2026-07-21. Scope: the scale of the problem in India, what existing products (commercial and open-source) actually do, the hard platform constraints on Android vs iOS, and the India-specific data protection rules that will shape the domain model.

## The problem, in numbers

- Adolescents in India average **4–5 hours/day** on screens.
- A 2024 government-backed survey found nearly **90% of children aged 14–16** have access to a smartphone at home; the share of school-going children owning a smartphone grew from **36% to 61%** in two years.
- A study of secondary school children in rural India found **83.2%** showed excess screen time.
- The Economic Survey 2025–26 calls digital addiction "a silent drag on learning, mental health, and economic output."

### Ownership vs. access — how many children have a dedicated device?

This matters directly for scoping: a "child's own device" enforcement model only reaches children who actually have one. ASER 2024 (rural India) found:

- Only **27% of 14-year-olds** and **37.8% of 16-year-olds** *own* their own phone.
- Nearly **90%** of the same age group have *access* to a smartphone at home — i.e. a shared family device, not one they own.

So individual ownership among teens is a meaningful and growing minority (rising with age, and likely higher in urban/middle-class households than this rural-focused survey captures), not the majority. Most children's smartphone exposure in India happens on a **shared or parent-owned device**, which is why the product can't only target the "child has their own phone" persona — see the [tentative v1 feature set](../../brainstorm/v1-feature-set.md) for how this splits into personas.

### Toddlers and pre-schoolers: real, well-documented, and starting in infancy

The gap flagged during v1 brainstorming — that the stats above only cover the 14–16 age group — turned out to be an easy fix: Indian pediatric literature on under-5 screen time is substantial, it just isn't captured by ASER-style ownership surveys. This is not merely anecdotal:

- Screen exposure starts far earlier than "toddler" suggests: **95.1%** of infants under 1 year showed screen exposure in one Indian study, and **99.7%** of children were exposed to screen-based media by 18 months, starting at a **median age of 10 months**.
- Prevalence of *excessive* screen time is high and rises with age: pooled screen time under age 2 is **~1.23 hours/day**; **50.5%** of children aged 2–4 exceed WHO's recommended limit (median ~65 minutes/day); a Karnataka study found **over 60%** of under-5s exposed to excess screen time; a western-India study of ages 2–6 found a mean of **2.7 hours/day**.
- **WHO/AAP guidance**, for comparison: WHO recommends no screen time under 1 year, none preferred at age 1, and a max of 1 hour/day (less preferred) for ages 2–4; AAP recommends no screens before 18 months and caps quality content around 1 hour/day for ages 2–5. Indian population averages already exceed this guidance before even counting the high end of the distribution (which is where anecdotal reports of several hours/day for a single toddler sit).
- **The specific mechanism behind the toddler persona is directly confirmed**: 53% of Indian parents surveyed hold a positive view of giving toddlers screen time specifically "for learning purposes and to keep the child engaged while [the parent is] doing household chores" — i.e. handing over a phone to occupy a young child is a common, normalized parenting practice in India, not an edge case.
- Worth carrying into product framing (global, not India-specific research): frequent use of devices specifically to *soothe* an upset child aged 3–5 is associated with increased emotional dysregulation, particularly in boys (JAMA Pediatrics / Michigan Medicine). The product should aim to time-box media that's already happening, not implicitly encourage using it as a soothing tool.

Net: the toddler persona is well-supported, not just anecdotal — and the underlying "hand over the phone to occupy the child" pattern specifically is confirmed by Indian parental-attitude research, not just observation.

## Existing products: tracking is solved, proactive blocking is not

Nearly every commercial product (Qustodio, FamiSafe, Kidslox, FamilyTime, Kids Mode, Google Family Link) offers **usage tracking + scheduled limits + remote/manual lock**. Some specifics:

- **Google Family Link**: app blocking is **not instant** — changes propagate in ~5 minutes or on next connectivity, not in real time. It does offer an immediate one-tap **remote** lock the parent must trigger manually.
- **Kidslox / FamilyTime / FamiSafe**: "Instant Pause" / "Family Pause" features exist, but these are parent-initiated remote actions or time-limit-triggered locks, not a device reacting the instant a child picks it up.
- None of the mainstream commercial products market true **proactive, automatic interception** the moment a restricted child profile picks up the phone (as opposed to "lock when the daily budget runs out").

**This is the actual gap**: tracking and after-the-fact/scheduled locking are commoditized; real-time proactive blocking on pickup is not something the mainstream players lead with.

### Open-source prior art (validates feasibility on Android)

A few small open-source Android projects already prove the mechanism works:

- **childscreentime/cst** — "unescapable screen blocking" via a fullscreen overlay that can't be bypassed, minute-accurate usage tracking, credit-based time allowances.
- **xMansour/KidSafe** — Family-Link-style controls, auto-locks the device when a daily timer is exceeded.
- **KidShield** — lightweight open-source parental control for Android.
- **JordyHers-org/Times-up-flutter** — Flutter-based, Android-only, monitoring-focused rather than blocking.

None of these combine the blocking mechanism with a real parent dashboard, analytics, multi-child/multi-platform support, or India-specific compliance — they're small, single-maintainer projects. That combination, done well and open-source, is the gap.

## Platform mechanics: why this is an Android-first problem

**Android** — proactive blocking is achievable with no root required, via:

- **AccessibilityService** to detect the foreground app / unlock events in real time and throw up a fullscreen blocking overlay. This is what all the open-source examples above use. Trade-offs: requires the user to manually grant an Accessibility permission (friction, but scriptable into a guided setup flow like Family Link's), and Play Store applies extra policy scrutiny to Accessibility API usage.
- **DeviceAdminReceiver + DevicePolicyManager** (`setKeyguardDisabled`, `force-lock`) to force-lock the screen. On Android 7+, a regular device-admin app can lock the screen but not change the password; deeper control requires **Device Owner** status, realistically only obtainable through a managed/enterprise-style provisioning flow at setup time.
- **UsageStatsManager / UsageEvents** for the real-time "pick up phone" trigger and for analytics.

**iOS** — this is a hard platform ceiling, not a competitor execution gap:

- The only path for third parties is Apple's **Screen Time API** stack: `FamilyControls`, `ManagedSettings`, `DeviceActivity`.
- Requires a special Apple **entitlement** (`com.apple.developer.family-controls`) granted through the developer portal.
- App identities are exposed as **opaque tokens** — a third-party app literally cannot know *which* app it's shielding by name.
- Extensions built on this API have **no network access** and run under tight background-execution limits.
- Requires Family Sharing + guardian approval; once approved, it can't be silently revoked by the child, but the third party still only gets "a carefully controlled subset" of what Apple's own built-in Screen Time can do internally.
- **Conclusion**: no third-party app — including every commercial competitor — can replicate Android-level proactive blocking on iOS. Any v1 scope decision should treat iOS support as "best-effort via Apple's official framework," not "at parity with Android."

## India-specific compliance: the DPDP Act is central, not a footnote

This product's core function — monitoring and restricting a child's device — is exactly the activity India's **Digital Personal Data Protection (DPDP) Act 2023** and its **2025 Rules** are built around:

- A "child" is defined as **under 18** (broader than COPPA's under-13).
- **Section 9(1)**: verifiable **parental consent is mandatory** before processing a child's personal data. **Rule 10** specifies approved verification methods, including integration with **DigiLocker** to verify the parent's identity and the parent-child relationship.
- **Prohibited**: tracking/monitoring/profiling of children for behavioral targeting, and any processing with a "detrimental effect" on a child's wellbeing.
- **Notable exemption**: real-time **location tracking for child safety** is exempted from the consent/no-tracking restrictions — relevant if a "where's my kid" feature is ever considered.
- **Penalties**: up to **₹200 crore** for violations of the children's-data provisions specifically.
- Compliance is required **18 months** after the rules' notification.

**Architectural implication**: consent and guardianship can't be bolted on later — they need to be first-class concerns in the domain model (a dedicated Consent/Guardianship bounded context) and enforced structurally (e.g., workflows that gate any data collection on consent state), not just a checkbox in onboarding.

## Takeaways for scoping v1

1. **Android is where the differentiated feature (instant proactive lock) is actually achievable**, and small open-source projects already prove the mechanism. Nobody has combined it with a polished parent dashboard, real analytics, and India-specific compliance.
2. **iOS parity is not possible** for any third party — this bounds what a v1 (or any version) can promise on iOS. Plan the roadmap and marketing claims accordingly.
3. The genuine market gap is: **open-source + India-focused + proactive blocking + DPDP-compliant consent model**, not "yet another usage tracker."

## Sources

- [Family Pause — FamilyTime](https://familytime.io/features/family-pause.html)
- [Best parental control apps 2026 — Tom's Guide](https://www.tomsguide.com/us/best-parental-control-apps,review-2258.html)
- [Manage your child's screen time — Google For Families Help](https://support.google.com/families/answer/7103340?hl=en)
- [Family Link limits and blocked apps don't persist — Android Community](https://support.google.com/android/thread/266912688/family-link-limits-and-blocked-apps-don-t-persist?hl=en)
- [A Developer's Guide to Apple's Screen Time APIs — Medium](https://medium.com/@juliusbrussee/a-developers-guide-to-apple-s-screen-time-apis-familycontrols-managedsettings-deviceactivity-e660147367d7)
- [Screen Time Technology Frameworks — Apple Developer Documentation](https://developer.apple.com/documentation/screentimeapidocumentation)
- [Configuring Family Controls — Apple Developer Documentation](https://developer.apple.com/documentation/xcode/configuring-family-controls)
- [GitHub — childscreentime/cst](https://github.com/childscreentime/cst)
- [GitHub — xMansour/KidSafe](https://github.com/xMansour/KidSafe)
- [KidShield — DEV Community](https://dev.to/ford_flatley_a001ae223a8a/kidshield-is-a-powerful-open-source-parental-control-solution-designed-for-android-devices-2k7a)
- [GitHub — JordyHers-org/Times-up-flutter](https://github.com/JordyHers-org/Times-up-flutter)
- [Prevalence of excess screen time among secondary school children in rural India — PMC](https://pmc.ncbi.nlm.nih.gov/articles/PMC10876028/)
- [India's social media user boom raises concerns over youth screen time — Storyboard18](https://www.storyboard18.com/digital/indias-social-media-user-boom-raises-concerns-over-youth-screen-time-88504.htm)
- [DeviceAdminReceiver — Android Developers](https://developer.android.com/reference/android/app/admin/DeviceAdminReceiver)
- [Controlling Android — HackMag](https://hackmag.com/mobile/android-api-2)
- [Rule 10 of the DPDP Rules 2025](https://www.dpdpa.com/dpdparules/rule10.html)
- [Parental Consent: DPDP Law on Children's Data](https://www.consent.in/blog/child-consent)
- [Children's Data Protection Under India's DPDP Rules](https://ksandk.com/data-protection-and-data-privacy/childrens-data-protection-under-indias-dpdp-rules/)
- [India's New Data Rules Exempt Real-Time Child Location Tracking From Parental Consent Mandate — MediaNama](https://www.medianama.com/2025/11/223-dpdp-rules-real-time-child-tracking-without-consent/)
- [Screen Time in Indian Children by 15-18 Months of Age — Indian Pediatrics](https://www.indianpediatrics.net/nov2020/1033.pdf)
- [Screen Time Among Under-Five Children in India: A Systematic Review and Meta-Analysis — PMC](https://pmc.ncbi.nlm.nih.gov/articles/PMC12229826/)
- [Prevalence, patterns and parental perceptions of excessive screen time among children aged 2-4 years — Indian Journal of Medical Research](https://ijmr.org.in/prevalence-patterns-and-parental-perceptions-of-excessive-screen-time-among-children-aged-2-4-years-a-cross-sectional-study-from-a-rural-setting-of-haryana-india/)
- [Prevalence of excessive screen time and its association with developmental delay in children aged <5 years — PMC](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8259964/)
- [Analysis of parental beliefs and practices leading to excessive screen time in early childhood — PMC](https://pmc.ncbi.nlm.nih.gov/articles/PMC12198805/)
- [New WHO guidance: Very limited daily screen time recommended for children under 5 — American Optometric Association](https://www.aoa.org/news/clinical-eye-care/public-health/screen-time-for-children-under-5)
- [Updated AAP recommendations for screen time — CHOC Children's Health Hub](https://health.choc.org/updated-aap-recommendations-for-screen-time/)
- [Frequently using digital devices to soothe young children may backfire — Michigan Medicine / ScienceDaily](https://www.sciencedaily.com/releases/2022/12/221212140614.htm)
