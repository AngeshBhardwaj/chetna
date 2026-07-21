# ADR-0011: OpenSpec/Node installed natively on the host — exception to ADR-0002

- **Status**: Accepted
- **Date**: 2026-07-21

## Context

[ADR-0002](0002-containerized-dev-tooling.md) established containerizing everything as the default, specifically to avoid ad hoc host-level installs. OpenSpec (the spec-driven-development CLI, `@fission-ai/openspec` on npm) needs Node.js to run. Following ADR-0002's default literally would mean invoking it via `docker run` on every use.

## Options considered

- **Containerize via `docker run` per invocation**, consistent with ADR-0002. Workable for a one-shot generation step, but OpenSpec is used constantly and interactively throughout ongoing spec-driven development (`openspec init`, drafting proposals, applying changes, archiving) — much closer in usage pattern to `git` or the `gh` CLI (both already host-installed) than to the JDK/Gradle/Android SDK build toolchain ADR-0002 was primarily written for.
- **Install Node natively on the host via nvm**, and OpenSpec globally via npm. Breaks the "no host installs" default, but matches how the tool is actually used.

## Decision

Node LTS installed via `nvm` (user-scoped, not a system-wide/sudo package manager install) and `@fission-ai/openspec` installed globally via npm. This is a deliberate, narrow exception to ADR-0002, scoped specifically to interactive authoring-tool CLIs used constantly across sessions — not a general reopening of "install things on the host when convenient."

Before installing, the package name was verified against the project's actual GitHub repository (`Fission-AI/OpenSpec`), since the plain `openspec` name on npm turned out to be an unrelated, squatted package from 2019, and several similarly-named packages (`openspec-buddy`, `openspec-shipper`) exist from unrelated, unverified maintainers. The package's postinstall script was read before being allowed to run (it only prints a shell-completion tip; no network calls or file writes).

## Consequences

- Node/npm and OpenSpec now exist on this host outside any container — a new machine or contributor needs to replicate this setup (`nvm install --lts && npm install -g @fission-ai/openspec@latest`) rather than getting it automatically via `docker compose`.
- Everything else (JDK, Gradle, Android SDK, the docs site) stays containerized per ADR-0002 — this exception doesn't extend to build toolchains.
- Package identity was verified against the source repository rather than trusting npm search results, given active name-squatting/lookalike packages for this specific tool.

## Revisit when

OpenSpec ships an official Docker image or devcontainer configuration that makes containerized usage frictionless for interactive, constant use — or if reproducibility/compliance needs later require enforcing "no host installs" without exception.
