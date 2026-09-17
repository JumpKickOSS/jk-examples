# spring-boot/petshop — multi-module Spring Boot

Workspace dogfood: **domain** → **service** → **web**, with real Boot tests.

| Module | Role |
|--------|------|
| `domain` | Pure `Pet` record (no Spring) |
| `service` | `@Service` `PetService` (in-memory store; DI-friendly) |
| `web` | `@SpringBootApplication`, REST `/api/pets`, actuator, **MockMvc** tests, `[image]` |

Spring Boot is `[spring-boot] version = "4.1.1"` on `web`; `spring-context` on `service` is
pinned to `7.0.9`, the version that Boot release manages. `jk-lock.toml` at the root holds the
whole workspace.

## What it exercises

- **`spring-boot-starter-webmvc-test`** is required for `@AutoConfigureMockMvc` (Boot 4
  modularization — `starter-test` alone is not enough); the import is
  `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`.
- Workspace edges: `{ workspace = true }` plus `@Import` / component scan across sibling jars.
- Root-level test tiers (`[test] exclude-tags` and one profile per tag) apply to every member.
- Guards: the `spring` pack for the framework and the `monorepo` pack for the workspace.
- `[build-info]` on `web`: the Boot jar carries `git.properties` and `META-INF/build-info.properties`,
  so `/actuator/info` reports the commit the build came from.
- **Known gap:** JPA repositories living only in a library jar scan as *0 interfaces* under this
  layout; the in-memory service keeps the multi-module DI path green.

```sh
jk build
jk test -m petshop-web
jk run             # GET http://localhost:8080/api/pets
jk guard
jk image -m petshop-web
```
