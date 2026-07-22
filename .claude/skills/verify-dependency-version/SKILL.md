---
name: verify-dependency-version
description: Verify a dependency's real latest/compatible version from authoritative sources before pinning it in a build file. Use before adding any new library/tool version, or when a freshly-added dependency fails with a cryptic error that might be a version-compatibility issue rather than a config/environment problem.
---

Package-manager search APIs can lag behind what's actually published, sometimes by major versions. Pinning against a stale "latest" can quietly introduce a compatibility gap with whatever else that library needs to work with — one that surfaces later as a confusing runtime error rather than an obvious version conflict.

## Before pinning any dependency version

1. **Don't trust a search index alone.** Cross-check against the authoritative registry source:
   - Maven: `https://repo1.maven.org/maven2/<group-path>/<artifact>/maven-metadata.xml` — read the `<latest>`/`<release>` tags directly.
   - npm: verify the package name against the project's actual GitHub repo/README, not just search results — generic names get squatted (see ADR-0011: `openspec` on npm is an unrelated package from 2019, the real one is `@fission-ai/openspec`).
2. **Check compatibility, not just recency.** If the library talks to (or is driven by) something else already fixed in the project — a daemon, a runtime, another major dependency — check that specific pairing's compatibility (changelog, release notes, GitHub issues) before assuming "latest" is safe.
3. **Don't default to an old major version out of inertia.** If a newer major version exists, check its changelog for breaking changes and prefer it unless there's a concrete reason not to.
4. **When something fails right after adding a dependency**, search early for `<library> <error> <adjacent fixed dependency + its version>` compatibility issues, before spending time on environment/infrastructure theories — version mismatches often produce errors that look environmental.
