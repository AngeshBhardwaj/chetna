# config

External runtime configuration, per [Spring Boot's own convention](https://docs.spring.io/spring-boot/reference/features/external-config.html) of loading `./config/` relative to the working directory, in addition to what's packaged in the jar. Use this for environment-specific overrides (ops-owned tuning, deployment-specific values) that shouldn't require a rebuild — not for secrets, which belong in a secrets manager, not a file.

Empty for now; `src/main/resources/application.yml` already covers local/docker-compose config via environment variables with sensible defaults.
