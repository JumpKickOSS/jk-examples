# kotlin/ktor-petshop — Ktor + Koin + Exposed

An idiomatic multi-module Kotlin pet shop:

| Module | Role |
|--------|------|
| `domain` | `Pet` data class + `PetRepository` interface, package `com.example.petshop.domain`; a library, so `jk build` also writes its sources and javadoc jars, `[build-info]` puts `git.properties` in the jar |
| `app` | Ktor Netty, **Koin** DI, **Exposed** + **H2**, REST `/api/pets`, `testApplication` tests, `[image]` |

Everything floats: `kotlin = "latest"` at the root, and every Ktor, Koin, Exposed, H2 and
Logback coordinate is `latest`. `jk-lock.toml` pins them (Exposed is on its 1.x API,
`org.jetbrains.exposed.v1.*`).

It exercises:

- a Kotlin workspace with one root lock and inherited identity
- root-level test tiers (`[test] exclude-tags`, one profile per tag)
- the `monorepo` guard pack — `one-module-per-package` is why `domain` has its own package —
  plus a per-language file-size ratchet (`cap = { java = 800, kt = 600 }`)
- a Kotlin library's javadoc jar: `domain` has no `[application]`, so `jk build` writes
  `ktor-petshop-domain-0.1.0-sources.jar` and `-javadoc.jar` beside its jar, and the javadoc jar is
  **Dokka**'s javadoc-format output (a pinned, cached tool; `[dokka] version` moves the release),
  what a `jk publish --central` release ships

```sh
jk build
jk test -m ktor-petshop-app
jk run             # GET http://localhost:8080/api/pets
jk guard
jk image -m ktor-petshop-app
```
