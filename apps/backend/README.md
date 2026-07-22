# backend

Spring Boot modular monolith. See the [ADR index](../../docs/content/documentation/adr/index.md) for the architecture decisions behind this module.

## Running tests locally

```bash
# unit tests
docker run --rm -v "$(pwd)/..:/apps" -w /apps gradle:9.6.1-jdk21 gradle :backend:test

# integration tests (Testcontainers-backed, needs the Docker socket)
docker run --rm \
  -v "$(pwd)/..:/apps" \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -e DOCKER_HOST=unix:///var/run/docker.sock \
  -e TESTCONTAINERS_RYUK_DISABLED=true \
  -e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal \
  -w /apps gradle:9.6.1-jdk21 gradle :backend:integrationTest
```

The two `TESTCONTAINERS_*` env vars are required specifically because the build runs *inside* a container talking to the host's Docker daemon via the mounted socket (a "sibling containers" setup), which is how this project's containerize-everything convention (`ADR-0002`) works. Without them:

- Ryuk (Testcontainers' cleanup sidecar) can't connect back to the build container across Docker Desktop's networking, failing with `Could not connect to Ryuk`. Disabling it is safe here — Testcontainers still cleans up via a JVM shutdown hook on normal exit, which is how these builds always terminate. (If a build ever gets forcibly killed rather than exiting normally, run `docker container prune` to clear any orphaned containers.)
- Without the host override, Testcontainers can't reach the Postgres container it starts (`Connection refused` against the default bridge gateway IP) — a Docker Desktop-specific quirk where the exposed port isn't reachable the way it would be on a native Linux Docker host.

**Platform-specific — Docker Desktop only (Windows/Mac), not universal.** Both env vars exist to work around Docker Desktop's VM-backed networking; they're not a WSL-only quirk, but they also aren't needed on a native Linux Docker host (e.g. most Linux CI runners), where the default bridge networking usually works without them. `host.docker.internal` in particular isn't defined by default on native Linux — don't copy this into a Linux CI pipeline without checking whether it's actually needed there first. Never used in production: Testcontainers is a test-only dependency (scoped to `integrationTest`), absent from the production jar and from the `docker-compose.yml` `backend` service.

`e2eTest` needs the full stack running (`docker compose up`) and a `BACKEND_BASE_URL` pointing at it; not run as part of `check`.
