# spring-boot/petshop — multi-module Spring Boot 4.1 (JK-1170)

Workspace dogfood: **domain** → **service** → **web** with real Boot 4 tests.

## Layout

| Module | Role |
|--------|------|
| `domain` | Pure `Pet` record (no Spring) |
| `service` | `@Service` `PetService` (in-memory store; DI-friendly) |
| `web` | `@SpringBootApplication`, REST `/api/pets`, actuator, **MockMvc** tests |

## Boot 4 edges this sample exercises

- **`spring-boot-starter-webmvc-test`** is required for `@AutoConfigureMockMvc` (Boot 4 modularization — `starter-test` alone is not enough).
- Import package: `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`.
- Multi-module: workspace `{ workspace = true }` + `@Import` / component scan across sibling jars.
- **JPA repositories living only in a library jar** currently scan as *0 interfaces* under this layout (Spring Data + workspace jars) — left as a known follow-up; in-memory service keeps the multi-module DI path green.

## Run

```bash
jk lock
jk build
jk test --modules web
jk run -C web
# GET http://localhost:8080/api/pets
```

Spring Boot **4.1.0** via `[spring-boot] version` on `web`.
