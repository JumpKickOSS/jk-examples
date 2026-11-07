# 02-services/helidon-petshop — multi-module Helidon

The pet shop of [`spring-boot-petshop`](../spring-boot-petshop) and [`ktor-petshop`](../ktor-petshop),
written the way Helidon recommends: Helidon SE with **Helidon Declarative**, its services wired by the
service registry at build time, and **Helidon DB Client** over an in-memory H2.

| Module | Role |
|--------|------|
| `domain` | Pure `Pet` record (no Helidon) |
| `service` | `PetService` and `PetDatabase`: registry services (`@Service.Singleton`) over Helidon DB Client |
| `web` | `PetEndpoint` (`@RestServer.Endpoint`) at `/api/pets`, Helidon Validation, health and metrics, `Main` with `@Service.GenerateBinding`, `[image]` and `[jlink]` |

## The JDK it needs, installed for you

Helidon's jars are compiled for a newer Java than most machines have. `java` in `jk.toml` pins that
release, and on a machine without it the first `jk build` downloads and installs it: there is
nothing to set up first. The compiler and the test JVMs run on it; jk's own engine stays on its JDK.

```sh
jk build          # installs the pinned JDK if this machine lacks it, then builds and tests
```

On a machine without it, the build says so once and goes on:

```
W installed JDK temurin-… to ~/.jdks/temurin-…
OK build petshop
```

## Run it

```sh
jk run -m petshop-web
curl -s localhost:8080/api/pets
curl -s 'localhost:8080/api/pets?species=cat'
curl -si -X POST -H 'Content-Type: application/json' -d '{"name":"Rex","species":"dog"}' localhost:8080/api/pets
curl -s localhost:8080/api/pets/3
curl -si localhost:8080/observe/health/ready
curl -s localhost:8080/observe/metrics | grep PetEndpoint
```

The POST answers `201 Created` with a `Location` header; a blank `name` is a `400` from Helidon
Validation before the endpoint runs.

## What it exercises

- **The `[helidon]` plugin.** `[helidon] version` imports Helidon's BOM and its annotation processors;
  `declarative = true` adds Helidon's Declarative code generators. In `web`, `@Service.GenerateBinding`
  on `Main` makes the build write the real `ApplicationBinding` from Helidon's own generators: every
  service of the workspace (the endpoint and health check in `web`, `PetService` and `PetDatabase` in
  `service`) bound at build time, so the shop starts without scanning the classpath. `validate = true`
  checks the injection graph at build time, as `helidon-service-maven-plugin` does.
- **Declarative REST.** `PetEndpoint` is annotations only: `@Http.GET`/`@Http.POST`, a query parameter,
  a path parameter, `@RestServer.Status(201)`, `@Metrics.Counted`. The routing is generated.
- **Helidon Validation and JSON binding.** `NewPet` is a `@Validation.Validated`, `@Json.Entity`
  record; Helidon generates its validator and its JSON converter at build time.
- **Helidon DB Client.** Every SQL statement is named in `application.yaml` beside the connection;
  `PetDatabase` supplies the one `DbClient` to whatever injects it.
- **Tests.** `@ServerTest` starts the registry's real services on a free port, under Helidon's `test`
  config profile (`application-test.yaml`), which `[helidon]` turns on for the test JVM.
- **`jk dev`.** `jk dev` serves the shop and restarts it on an edit to any of the three modules.
- **Packaging.** `jk build` also links `web/target/petshop-web-jri/`, a runtime image with an AOT
  cache (`[jlink]`); `jk image -m petshop-web` builds the container image (`[image]`).
- **Guards.** The `helidon` pack (no MicroProfile, Jakarta not `javax`, test support in the test
  scope) and the `monorepo` pack.

```sh
jk build
jk test -m petshop-web
jk guard
jk dev
web/target/petshop-web-jri/bin/start
jk image -m petshop-web
```
