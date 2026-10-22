# 02-services/quarkus-rest — a Quarkus service on PostgreSQL

`talks` is a conference schedule API: Quarkus REST with Jackson, Hibernate ORM with Panache,
Bean Validation, Flyway migrations and health checks on PostgreSQL. It starts from
`jk new -t quarkus/hello` and keeps that shape: one `jk.toml`, no `pom.xml`, no Maven wrapper.

```toml
[quarkus]
version = "3.40.1"      # the platform BOM; every io.quarkus extension below is versionless
```

jk runs Quarkus's own build: the `quarkus-augment` step bootstraps Quarkus from the lock and writes
the fast-jar tree (`target/quarkus-app/`), and `quarkus-test-model` writes the application model
`@QuarkusTest` boots from. The lock decides every version Quarkus sees.

```sh
jk build                     # compile, unit tier (plain JUnit), augment → target/quarkus-app/
jk test -g                   # @QuarkusTest on PostgreSQL 18: Dev Services starts it in Docker or Podman
jk dev                       # Quarkus dev mode; Dev Services starts the database
curl 'localhost:8080/talks?track=jvm'
jk image                     # OCI image of the fast-jar tree (non-root, liberica JRE 25)
jk native                    # Quarkus's native build on the GraalVM jk provisions
```

## Layout

| Path | What |
|---|---|
| `src/main/java/…/Talk.java` | Active-record Panache entity; the database assigns the id. |
| `src/main/java/…/Slot.java` | The one domain rule: two talks in a room must not overlap. |
| `src/main/java/…/TalkResource.java` | `GET/POST /talks`, `GET/DELETE /talks/{id}`; 409 on a clash, 400 on invalid input. |
| `src/main/resources/db/migration/` | Flyway owns the schema; Hibernate only validates it. |
| `src/test/` | The unit tier: plain JUnit on `Slot`, milliseconds, no container. |
| `src/integration/` | `jk test -g`: the API through RestAssured on a real PostgreSQL. |

## Configuration

`application.properties` names PostgreSQL once. In `jk dev` and in the integration tier no URL is
set, so Quarkus Dev Services starts `postgres:18-alpine`. With Podman, point Testcontainers at
its socket (`DOCKER_HOST`) and set `TESTCONTAINERS_RYUK_DISABLED=true`. In production (`%prod`) the
URL and credentials come from `DATABASE_URL`, `DATABASE_USER` and `DATABASE_PASSWORD`:

```sh
docker run --rm -p 8080:8080 -e DATABASE_URL=jdbc:postgresql://db:5432/talks quarkus-rest:0.1.0
```

## Teaches

- A Quarkus app in `jk.toml`: `[quarkus] version`, versionless extensions, test libraries on
  `[test-dependencies]` only.
- Tiers by directory: logic in `src/test`, anything that needs a container in `src/integration`.
- `[native] enabled = true` makes `jk native` available without changing what `jk build` ships.

## Validates

- `quarkus-augment` with a datasource, Flyway and Hibernate (the extension closure from the lock).
- `quarkus-test-model` for `@QuarkusTest` with Dev Services through Testcontainers.
- The fast-jar tree as the artifact and the image layer, with `[native]` declared.
