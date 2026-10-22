# 02-services/ktor-petshop — Ktor + Koin + Exposed

An idiomatic multi-module Kotlin pet shop:

| Module | Role |
|--------|------|
| `domain` | `Pet` data class + `PetRepository` interface, package `com.example.petshop.domain`; a library, so `jk build` also writes its sources and javadoc jars, `[build-info]` puts `git.properties` in the jar |
| `app` | Ktor Netty, **Koin** DI, **Exposed** + **H2**, REST `/api/pets`, `testApplication` tests, `[image]` |

Every version is an exact pin: `kotlin = "2.4.21"` at the root, Ktor `3.6.0`, Koin `4.2.2`,
Exposed `1.6.0` (its 1.x API, `org.jetbrains.exposed.v1.*`), H2 `2.5.252` and Logback `1.6.5`
in `app`, `[dokka] version = "2.2.0"` in `domain`; `jk update` moves them all.

It exercises:

- a Kotlin workspace with one root lock and inherited identity
- root-level test tiers (`[test] exclude-tags`, one profile per tag)
- the `monorepo` guard pack — `one-module-per-package` is why `domain` has its own package —
  plus a per-language file-size ratchet (`cap = { java = 800, kt = 600 }`)
- a Kotlin library's javadoc jar: `domain` has no `[application]`, so `jk build` writes
  `ktor-petshop-domain-0.1.0-sources.jar` and `-javadoc.jar` beside its jar, and the javadoc jar is
  **Dokka**'s javadoc-format output (a pinned, cached tool; `domain`'s `[dokka] version` is the
  release), what a `jk publish --central` release ships
- one `Application.module()` shared by `main` and the `testApplication` test, which hands it its
  own in-memory database

```sh
jk build
jk test -m ktor-petshop-app
jk run             # GET http://localhost:8080/api/pets (PORT overrides 8080)
jk guard
jk image -m ktor-petshop-app
```
