# ADR-0008: Mobile design system — platform-native + custom brand theme

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

Having committed to native UI per platform rather than a shared cross-platform UI layer ([ADR-0003](0003-mobile-architecture.md)), the app needs a concrete design-system answer: build a custom component set from scratch, or adopt each platform's own design language. For a parental-control app specifically, feeling native and trustworthy on the parent's and child's own device matters — an app that visually feels foreign to the OS works against that trust.

## Options considered

- **One unified custom design system across both platforms**: a single custom component set, uniform look-and-feel on both Android and iOS, overriding each platform's native conventions. Maximizes cross-platform brand uniformity, at the cost of significant design and engineering effort to build and maintain a full component library from scratch on two platforms, and a UI that inevitably feels non-native on at least one of them.
- **Platform-native design systems + a custom brand theme layered on top**: Material Design 3 on Android (via Compose's official `material3` library — free, open-source, full component set, theming/dark-mode/dynamic-color built in) and Apple's Human Interface Guidelines via SwiftUI's native components on iOS. Chetna's own colors, typography, iconography, and tone applied as a theme over each platform's native components, rather than replacing them.

## Decision

Platform-native design systems (Material 3 on Android, native HIG/SwiftUI components on iOS), with a custom Chetna brand theme layered on top of each. The actual brand theme (palette, typography, iconography, tone of voice) and the product's genuinely novel screens — the toddler hand-over screen, the "soft lock" ambient-dark screen — are a dedicated design exercise, tracked under [Design](../../design/index.md), not decided as part of this architectural choice.

## Consequences

- No custom component library to build and maintain from scratch on two platforms — Material 3 and SwiftUI both ship complete, accessible, well-tested component sets for free.
- The app feels native and trustworthy on each OS rather than like a foreign cross-platform skin.
- Brand consistency across platforms is achieved through a shared theme (colors/typography/tone) rather than shared components — some visual differences between Android and iOS are expected and acceptable, since each follows its own platform's native conventions underneath.

## Revisit when

The product needs UI elements no reasonable theming of Material 3 or native SwiftUI components can express well — that's the trigger to consider custom components for that specific case, not a wholesale reconsideration of this decision.
