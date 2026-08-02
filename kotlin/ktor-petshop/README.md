# kotlin/ktor-petshop — Ktor + Koin + Exposed (JK-1171)

Idiomatic multi-module Kotlin pet shop:

| Module | Role |
|--------|------|
| `domain` | `Pet` data class + `PetRepository` interface |
| `app` | Ktor Netty, **Koin** DI, **Exposed** + **H2**, REST `/api/pets`, `testApplication` tests |

## Stack

- Ktor server (Netty) + Jackson content negotiation  
- Koin (`koin-ktor`) for repository injection  
- Exposed JDBC + in-memory H2  
- JUnit 5 + `ktor-server-test-host`

## Run

```bash
jk lock
jk build
jk test --modules app
jk run -C app
# GET http://localhost:8080/api/pets
```
