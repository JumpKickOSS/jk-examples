# 02-services/spring-boot-petshop — multi-module Spring Boot

Workspace dogfood: **domain** → **service** → **web**, with real Boot tests.

| Module | Role |
|--------|------|
| `domain` | Pure `Pet` record (no Spring) |
| `service` | `@Service` `PetService` (in-memory store; DI-friendly) |
| `web` | `@SpringBootApplication`, REST `/api/pets`, actuator, **MockMvc** tests, `[image]` |

Boot's BOM is the workspace's platform: the root's `[platform-dependencies]
spring-boot-dependencies = "4.1.1"` reaches every member, so `service` declares
`spring-context = "managed"` and the lock records the version Boot 4.1.1 manages. `web` carries
`[spring-boot] version = "4.1.1"`, the same release, for the Boot jar's loader and AOT tools;
keep the two pins on one release.
`jk-lock.toml` at the root holds the whole workspace.

## What it exercises

- **`spring-boot-starter-webmvc-test`** is required for `@AutoConfigureMockMvc` (Boot 4
  modularization — `starter-test` alone is not enough); the import is
  `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`.
- Workspace edges: `{ workspace = true }`, and `@SpringBootApplication`'s own component scan of
  `com.example.petshop` finding `PetService` in the sibling `service` jar.
- Root-level test tiers (`[test] exclude-tags` and one profile per tag) apply to every member.
- Guards: the `spring` pack for the framework and the `monorepo` pack for the workspace.
- `[build-info]` on `web`: the Boot jar carries `git.properties` and `META-INF/build-info.properties`,
  so `/actuator/info` reports the commit the build came from.

```sh
jk build
jk test -m petshop-web
jk run             # GET http://localhost:8080/api/pets
jk guard
jk image -m petshop-web
```
