# Module map — Helidon 27.0.1 under jk

The workspace is **Helidon's whole default Maven reactor**: what `mvn install` builds from the root
POM with no profile, as `jk import` reads it. 223 jk modules (about 4,300 main Java sources, 274 of
them JPMS modules with a `module-info.java`), 417 locked artifacts.

| Group | Modules | Notes |
|-------|--------:|-------|
| common | 26 | Core utilities; `common/common` carries the `Version` template (hand edit 2) |
| webserver | 32 | Nima webserver, observe, testing (junit5), grpc, websocket, security |
| webclient | 19 | |
| security | 18 | Providers (OIDC, JWT, HTTP auth/sign, ABAC) |
| data | 16 | Helidon Data, its codegen and the JDBC/JPA/datasource backends |
| http | 15 | HTTP model, encodings, media (JSON-B, JSON-P, Jackson, multipart, Helidon JSON) |
| config | 10 | Config, YAML/HOCON, encryption, metadata codegen |
| dbclient | 10 | DB Client, JDBC/Hikari/MongoDB, health, metrics, tracing |
| codegen | 9 | Helidon's annotation-processing framework; built here and used as processors by the rest |
| metrics, json | 6 each | |
| service, builder, bundles, openapi, metadata, testing, tracing, logging, telemetry, transaction | 3–5 each | `service` holds the service registry and its codegen |
| applications, graphql, grpc, health, declarative, discovery | 2 each | |
| all, fault-tolerance, helidon, jsonrpc, licensing, messaging, parent, scheduling, validation, websocket | 1 each | |

Not in the workspace, as not in Maven's default reactor: `tests/` (profile `tests`), `examples/`,
`archetypes/` and `javadocs/` (release profiles), `docs/`. The two BOMs (`bom`, `dependencies`) are
not modules either: no member imports them as a BOM, so the import reads their versions instead.

## What builds against what

The interesting edges are the processor paths. About 200 modules run Helidon's own annotation
processors — `codegen/apt`, `builder/codegen`, `service/codegen`, `config/metadata/codegen`,
`common/features/codegen`, `codegen/api-stability-enforcer` — and those processors are workspace
members built in the same run. `jk import` writes them as `[processor-dependencies]` with
`workspace = true`, so jk orders each module after the processors it runs, and `[build]
module-services` writes the `META-INF/services` registration each processor jar needs to be found.
