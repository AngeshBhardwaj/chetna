# ADR-0003: Mobile app built with Kotlin Multiplatform + native UI per platform

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

The v1 differentiator (real-time pickup detection, fullscreen soft-lock, Device Admin force-lock) depends entirely on deep, Android-specific OS integration: AccessibilityService, DeviceAdminReceiver/DevicePolicyManager, UsageStatsManager, and eventually `startLockTask` for the toddler kids-mode persona. iOS (phase 2) depends on an entirely different, more restricted framework (FamilyControls/ManagedSettings/DeviceActivity). The mobile approach needs to serve both without weakening the parts that matter most: reliability of the core enforcement mechanic.

## Options considered

- **Flutter or React Native**, with custom native plugins bridging to the OS-specific APIs above. One shared UI codebase, but nearly every capability that actually matters for this product (background service survival against OEM battery optimizers, real-time accessibility events, device admin) would need a hand-written native plugin bridge anyway — undermining the "write once" benefit exactly where reliability matters most.
- **Kotlin Multiplatform (KMP) for shared domain logic, fully native UI/platform-integration per OS** (Kotlin + Jetpack Compose on Android, Swift/SwiftUI on iOS when built). More upfront work (two UI codebases eventually), but unrestricted, first-class access to each platform's OS APIs exactly where the product's core value lives, and Clean Architecture's domain/use-case layer is written once and genuinely shared, not just conceptually mirrored.

## Decision

Use Kotlin Multiplatform for the shared domain/use-case layer. Android v1 ships as a native Kotlin + Jetpack Compose app. iOS (phase 2) ships as a native Swift/SwiftUI app, sharing the KMP domain module.

## Consequences

- No plugin-bridge layer to fight for the features that matter most; full native reliability for AccessibilityService/DeviceAdmin work.
- Two UI codebases to maintain long-term (Android, iOS) rather than one, once iOS actually starts.
- Domain logic (entities, use cases, validation rules) is written once in Kotlin and can also be reused by a Kotlin/JVM backend (see [ADR-0005](0005-backend-technology-stack.md)), reducing duplicate business-rule implementations across mobile and backend.

## Revisit when

If Apple or Google ship a first-party cross-platform framework or SDK that grants genuinely unrestricted access to AccessibilityService-equivalent or Screen-Time-equivalent APIs from a shared codebase (not currently the case for Flutter/React Native), or if iOS phase 2 work reveals the KMP/native-Swift split isn't paying for itself in practice.
